package ktml

/**
 * A graph containing nodes and edges.
 *
 * Assumes that nodes have order and that methods adhere to this order. For example, `getNode` by index returns
 * the same node as what can be expected from referencing a node in `nodes` by index.
 *
 * @param N the type of graph nodes
 * @param E the type of graph edges
 */
interface Graph<N, E> {
    val nodes: List<N>
    val edges: List<E>

    /**
     * The number of nodes in the graph.
     */
    val order: Int

    /**
     * The number of edges in the graph.
     */
    val size: Int

    /**
     * Add a node with no links to other nodes.
     * @return a `Result` containing a Unit on success or failure if the node is already in the graph (nodes must be
     * unique).
     */
    fun addNode(node: N): Result<Unit>

    /**
     * Set the graph nodes to a set of nodes. This also removes all existing edges and connections between nodes.
     */
    fun setNodes(nodes: Set<N>)

    /**
     * Check if a node exists in the graph.
     */
    fun hasNode(node: N): Boolean

    /**
     * Get the node at a node index location.
     *
     * @return a `Result` containing the node or failure if the node index is out of bounds.
     */
    fun getNode(nodeIndex: Int): Result<N>

    /**
     * Get the index of a node.
     *
     * @return the index of the node or -1 if it could not be found in the graph.
     */
    fun getNodeIndex(node: N): Int

    /**
     * Get the adjacent child nodes of a node.
     *
     * @return a `Result` containing a list of nodes or error if the node could not be found in the graph.
     */
    fun childNodes(node: N): Result<List<N>>

    /**
     * Get the parent nodes of a node.
     *
     * @return a `Result` containing a list of nodes or error if the node could not be found in the graph.
     */
    fun parentNodes(node: N): Result<List<N>>

    /**
     *  Add an edge between nodes.
     *
     *  @param fromNode the node from which the edge originates
     *  @param toNode the node to which the edge points
     *  @param edge the edge object that contains edge attributes
     *  @return a `Result` `Unit` or failure if a node could not be found in the graph.
     */
    fun addEdge(fromNode: N, toNode: N, edge: E): Result<Unit>

    /**
     *  Update an edge between nodes.
     *
     *  @param fromNode the node from which the edge originates
     *  @param toNode the node to which the edge points
     *  @param edge the edge object that contains edge attributes
     *  @return a `Result` `Unit` or failure if a node could not be found in the graph.
     */
    fun updateEdge(fromNode: N, toNode: N, edge: E): Result<Unit>

    /**
     * Check if two nodes are connected.
     *
     * @param fromNode the source node
     * @param toNode the target node
     * @return a `Result` containing true or false or a failure if `fromNode` or `toNode` could not be found in the graph.
     */
    fun isConnected(fromNode: N, toNode: N): Result<Boolean>

    /**
     * Get the edges from a node to other nodes.
     *
     * @return a `Result` containing a list of edges and the nodes they point to or a failure if the node could not be
     * found in the graph.
     */
    fun edgesFrom(node: N): Result<List<Pair<E, N>>>

    /**
     * Get the edges to a node specified by its index from other nodes.
     *
     * @return a `Result` containing a list of edges and the nodes indexes they point from or a failure if the index is
     * out of bounds.
     */
    fun edgesTo(node: N): Result<List<Pair<E, N>>>

    /**
     * Get the edges connecting two nodes.
     *
     * @return a `Result` containing a list of edges or failure if the nodes could not be found.
     */
    fun edgesBetween(fromNode: N, toNode: N): Result<List<E>>

    /**
     * Find the shortest path between nodes using Dijkstra's algorithm, taking into account edge weights.
     *
     * @param fromNode start of the path
     * @param toNode target node for the path to end on
     * @param getDistance a function to extract a distance from an edge and return a Double value. This enables
     * typesafe handling of different types of edges.
     * @return a `Result` containing a list of node indexes representing the shortest path or a failure if indexes are out
     * of bounds or if a path could not be found.
     */
    fun shortestPathDijkstra(fromNode: N, toNode: N, getDistance: (E) -> Double): Result<List<N>> {
        if (!hasNode(fromNode) || !hasNode(toNode)) return Result.failure(IllegalArgumentException("Node could not be found in the graph."))
        val dist = (0..<this.order).map { Double.MAX_VALUE }.toMutableList()
        dist[getNodeIndex(fromNode)] = 0.0
        val queue = nodes.toMutableList()
        val prev = (0..<this.order).map { 0 }.toMutableList()
        var current: N
        while (queue.isNotEmpty()) {
            // Find the node in queue with the lowest distance
            current = queue.map { it to dist[getNodeIndex(it)] }.minBy { it.second }.first
            queue.remove(current)
            // Check each neighbour and update the distance table with the shortest distance.
            val neighbours = this.childNodes(current).getOrNull() ?: emptyList()
            for (neighbour in neighbours) {
                if (queue.contains(neighbour)) {
                    val edges = edgesBetween(current, neighbour).getOrNull() ?: emptyList()
                    // Get minimum edge distance
                    val nDist = edges.minOfOrNull { getDistance(it) } ?: 0.0
                    if (nDist <= 0) return Result.failure(IllegalArgumentException("Dijkstra's algorithm expects positive weights."))
                    val currentIndex = getNodeIndex(current)
                    val neighbourIndex = getNodeIndex(neighbour)
                    val workingDist = dist[currentIndex] + nDist
                    if (workingDist < dist[neighbourIndex]) {
                        dist[neighbourIndex] = workingDist
                        prev[neighbourIndex] = currentIndex
                    }
                }
            }
        }
        if (dist[getNodeIndex(toNode)] == Double.MAX_VALUE) return Result.failure(NoSuchElementException("Could not find a path."))
        // Extract the path from the closest node list (prev).
        val path = mutableListOf(toNode)
        current = toNode
        while (current != fromNode) {
            val currentIndex = getNodeIndex(current)
            val next = getNode(prev[currentIndex]).getOrThrow()
            path.add(next)
            current = next
        }
        path.reverse()
        return Result.success(path)
    }

    /**
     * Calculate the sum of edge metrics in a path defined by nodes. If there are multiple edges between
     * two nodes, take the lowest value of the edge metric.
     *
     * @param nodes a List of node objects
     * @param getWeight a function to extract a metric from an edge and returns a `Double`
     * @return a `Result` containing the sum of edge weights on the path or an error if a node could not be found in the
     * graph. Also returns a failure if the path is invalid due to a direct step between unconnected nodes.
     */
    fun pathWeight(nodes: List<N>, getWeight: (E) -> Double): Result<Double> {
        var weight = 0.0
        var stepWeight: Double
        for ((current, next) in nodes.zipWithNext()) {
            val children = childNodes(current).getOrNull() ?: emptyList()
            if (!children.contains(next)) return Result.failure(IllegalArgumentException("Steps only allowed between connected nodes."))
            val edges = edgesBetween(current, next).getOrNull() ?: emptyList()
            stepWeight = edges.minOf { getWeight(it) }
            if (stepWeight == 0.0) return Result.failure(IllegalArgumentException("Steps between unconnected node not allowed."))
            weight += stepWeight
        }
        return Result.success(weight)
    }

    /**
     * Generate a path to walk along edges to all nodes possible using a breadth first approach.
     *
     * @param fromNode the starting node for the walk
     * @param maxSteps the maximum number of steps to take after which the walk is stopped
     * @return a `Result` containing a list of nodes representing the path walked or a failure if the start node
     * can not be found in the graph.
     */
    fun walkBreadthFirst(fromNode: N, maxSteps: Int): Result<List<N>> {
        if (!hasNode(fromNode)) return Result.failure(IllegalArgumentException("Node could not be found in the graph."))
        val path = mutableListOf<N>()
        val visited = (0..<this.order).map { false }.toMutableList()
        val queue = mutableListOf<N>()
        var steps = 0
        var current = fromNode
        var currentIndex = getNodeIndex(fromNode)
        while (steps <= maxSteps) {
            // Visit the node
            path.add(current)
            visited[currentIndex] = true
            // Find the neighbours and add unvisited neighbours to the queue
            val neighbours = this.childNodes(current).getOrNull() ?: emptyList()
            val unvisitedNeighbours = neighbours.filter { !visited[getNodeIndex(it)] }
            if (unvisitedNeighbours.isEmpty() && queue.isEmpty()) {
                break
            }
            for (neighbour in unvisitedNeighbours) {
                if (!queue.contains(neighbour)) {
                    queue.add(neighbour)
                }
            }
            // Visit the next node in the queue
            current = queue.removeFirst()
            currentIndex = getNodeIndex(current)
            steps += 1
        }
        return Result.success(path)
    }

    /**
     * Generate a path to walk along edges to all nodes possible using a depth first approach.
     *
     * @param fromNode the starting node for the walk
     * @param maxSteps the maximum number of steps to take after which the walk is stopped
     * @param includeBacktrack include nodes re-visited through backtracking to avoid getting stuck
     * @return a `Result` containing a list of nodes representing the path walked or a failure if the start or
     * end node could not be found in the graph.
     */
    fun walkDepthFirst(fromNode: N, maxSteps: Int, includeBacktrack: Boolean = false): Result<List<N>> {
        if (!hasNode(fromNode)) return Result.failure(IllegalArgumentException("Node could not be found in the graph."))
        val path = mutableListOf<N>()
        val visited = (0..<this.order).map { false }.toMutableList()
        val stack = mutableListOf<N>()
        var steps = 0
        var current = fromNode
        var currentIndex = getNodeIndex(fromNode)
        while (steps <= maxSteps) {
            // Visit the node
            path.add(current)
            visited[currentIndex] = true
            // Find the neighbours and add unvisited neighbours to the top of the stack
            val neighbours = this.childNodes(current).getOrNull() ?: emptyList()
            val unvisitedNeighbours = neighbours.filter { !visited[getNodeIndex(it)] }.toList()
            if (unvisitedNeighbours.isEmpty() && stack.isEmpty()) {
                break
            }
            for (neighbour in unvisitedNeighbours.reversed()) {
                if (!stack.contains(neighbour)) {
                    stack.add(neighbour)
                }
            }
            // Visit the top of the stack and backtrack if not possible
            val newNode = stack.removeLast()
            if (!neighbours.contains(newNode) && includeBacktrack) {
                var pathIndex = path.size - 1
                while (pathIndex != 0) {
                    pathIndex -= 1
                    path.add(path[pathIndex])
                    val children = this.childNodes(path[pathIndex]).getOrNull() ?: emptyList()
                    if (children.contains(newNode)) pathIndex = 0
                }
            }
            current = newNode
            currentIndex = getNodeIndex(newNode)
            steps += 1
        }
        return Result.success(path)
    }
}

/**
 * A directed graph without node indexing capability.
 *
 * @param N the type of graph nodes
 * @param E the type of graph edges
 */
open class UnIndexedGraph<N, E> : Graph<N, E> {

    // Adjacency contain the mapping of edges between source nodes and target nodes. The indexes of 'adjacency'
    // correspond to the index  positions in 'nodes' and represents the source nodes. 'adjacency' contain the
    // indexes of the target nodes for each source node. Example:
    //
    //    _nodes     _adjacency    _edges
    //       a          [2, 3]       [e1, e2]
    //       b          [1]          [e3]
    //       c          [4]          [e4]
    //      ...         ...           ...
    //
    protected var _nodes = mutableListOf<N>()
    private var _edges = mutableListOf<MutableList<E>>()
    private var adjacencyList = mutableListOf<MutableList<Int>>()

    override val nodes: List<N>
        get() = _nodes.toList()

    override val edges: List<E>
        get() = _edges.flatten()

    override val order: Int get() = this._nodes.count()

    override val size: Int
        get() {
            return this.adjacencyList.sumOf { it.count() }
        }

    override fun toString(): String = "UnIndexedGraph[order=${this.order}, size=${this.size}]"

    /**
     * Export graph to Graphviz dot format.
     */
    fun toGraphVizDot(): String {
        var result = "digraph {\n"
        for (sourceIndex in adjacencyList.indices) {
            for (targetIndex in adjacencyList[sourceIndex]) {
                result = "$result  ${_nodes[sourceIndex]} -> ${_nodes[targetIndex]};\n"
            }
        }
        result = "$result}"
        return result
    }

    override fun addNode(node: N): Result<Unit> {
        if (this.hasNode(node)) return Result.failure(IllegalArgumentException("Nodes must be unique. Node $node already added."))
        this._nodes.add(node)
        this._edges.add(mutableListOf())
        this.adjacencyList.add(mutableListOf())
        return Result.success(Unit)
    }

    /**
     * Add multiple nodes with no links to other nodes.
     *
     * @return a `Result` `Unit` or failure if a node is already in the graph (nodes must be unique).
     */
    open fun addNodes(nodes: List<N>): Result<Unit> {
        for (node in nodes) {
            val result = this.addNode(node)
            if (result.isFailure) return result
        }
        return Result.success(Unit)
    }

    /**
     * Add multiple nodes with no links to other nodes.
     *
     * @return a `Result` containing a Unit on success or failure if a node is already in the graph (nodes must be unique).
     */
    open fun addNodes(vararg nodes: N) = addNodes(nodes.toList())

    override fun hasNode(node: N): Boolean = getNodeIndex(node) != -1

    override fun getNode(nodeIndex: Int): Result<N> {
        val maxIndex = this._nodes.lastIndex
        if (nodeIndex < 0 || nodeIndex > maxIndex) return Result.failure(IndexOutOfBoundsException("Node index out of bounds."))
        return Result.success(_nodes[nodeIndex])
    }

    override fun getNodeIndex(node: N): Int = this._nodes.indexOf(node)

    /**
     * Set the node at a node index location.
     *
     * @param nodeIndex the index position to set the node
     * @param node the Node to set at the specified index
     * @return a `Result` `Unit` or failure if the node index is out of bounds or the node already
     * exists.
     */
    open fun setNode(nodeIndex: Int, node: N): Result<Unit> {
        val maxIndex = this._nodes.lastIndex
        if (nodeIndex < 0 || nodeIndex > maxIndex) return Result.failure(IndexOutOfBoundsException("Node index out of bounds."))
        if (hasNode(node)) return Result.failure(IllegalArgumentException("Nodes must be unique. Node $node already exists."))
        this._nodes[nodeIndex] = node
        return Result.success(Unit)
    }

    override fun setNodes(nodes: Set<N>) {
        this._nodes = nodes.toMutableList()
        this._edges = MutableList(this._nodes.count()) { mutableListOf() }
        this.adjacencyList = MutableList(this._nodes.count()) { mutableListOf() }
    }

    override fun addEdge(fromNode: N, toNode: N, edge: E): Result<Unit> {
        val fromNodeIndex = getNodeIndex(fromNode)
        val toNodeIndex = getNodeIndex(toNode)
        if (fromNodeIndex == -1 || toNodeIndex == -1) return Result.failure(IllegalArgumentException("Node could not be found in the graph."))
        this.adjacencyList[fromNodeIndex].add(toNodeIndex)
        this._edges[fromNodeIndex].add(edge)
        return Result.success(Unit)
    }

    /**
     *  Add an edge between nodes by index in 'nodes'.
     *
     *  @param fromNodeIndex the index of the node from which the edge originates
     *  @param toNodeIndex the index of the node to which the edge points
     *  @param edge the edge object that contains edge attributes
     *  @return a `Result` `Unit` or failure if `fromNodeIndex` or `toNodeIndex` is out of bounds.
     */
    fun addEdge(fromNodeIndex: Int, toNodeIndex: Int, edge: E): Result<Unit> {
        val maxIndex = this._nodes.lastIndex
        if (fromNodeIndex > maxIndex || fromNodeIndex < 0) return Result.failure(IndexOutOfBoundsException("fromNodeIndex $fromNodeIndex out of bounds."))
        if (toNodeIndex > maxIndex || toNodeIndex < 0) return Result.failure(IndexOutOfBoundsException("toNodeIndex $toNodeIndex out of bounds."))
        this.adjacencyList[fromNodeIndex].add(toNodeIndex)
        this._edges[fromNodeIndex].add(edge)
        return Result.success(Unit)
    }

    override fun updateEdge(fromNode: N, toNode: N, edge: E): Result<Unit> {
        val fromNodeIndex = getNodeIndex(fromNode)
        val toNodeIndex = getNodeIndex(toNode)
        if (fromNodeIndex == -1 || toNodeIndex == -1) return Result.failure(IllegalArgumentException("Node could not be found in the graph."))
        return this.updateEdge(fromNodeIndex, toNodeIndex, edge)
    }

    /**
     *  Update an edge between nodes.
     *
     *  @param fromNodeIndex the node from which the edge originates
     *  @param toNodeIndex the node to which the edge points
     *  @param edge the edge object that contains edge attributes
     *  @return a `Result` `Unit` or failure if a node could not be found in the graph.
     */
    fun updateEdge(fromNodeIndex: Int, toNodeIndex: Int, edge: E): Result<Unit> {
        val maxIndex = this._nodes.lastIndex
        if (fromNodeIndex > maxIndex || fromNodeIndex < 0) return Result.failure(IndexOutOfBoundsException("fromNodeIndex $fromNodeIndex out of bounds."))
        if (toNodeIndex > maxIndex || toNodeIndex < 0) return Result.failure(IndexOutOfBoundsException("toNodeIndex $toNodeIndex out of bounds."))
        val edgeIndex: Int = this.adjacencyList[fromNodeIndex].indexOf(toNodeIndex)
        if (edgeIndex == -1) {
            this.adjacencyList[fromNodeIndex].add(toNodeIndex)
            this._edges[fromNodeIndex].add(edge)
        } else {
            this._edges[fromNodeIndex][edgeIndex] = edge
        }
        return Result.success(Unit)
    }

    override fun isConnected(fromNode: N, toNode: N): Result<Boolean> {
        val fromNodeIndex = getNodeIndex(fromNode)
        val toNodeIndex = getNodeIndex(toNode)
        if (fromNodeIndex == -1 || toNodeIndex == -1) return Result.failure(IllegalArgumentException("Node could not be found in the graph."))
        val result = this.adjacencyList[fromNodeIndex].contains(toNodeIndex)
        return Result.success(result)
    }

    /**
     * Check if two nodes are connected.
     *
     * @param fromNodeIndex the source node
     * @param toNodeIndex the target node
     * @return a `Result` containing true or false or a failure if `fromNodeIndex` or `toNodeIndex` are out of bounds.
     */
    fun isConnected(fromNodeIndex: Int, toNodeIndex: Int): Result<Boolean> {
        val maxIndex = this._nodes.lastIndex
        if (fromNodeIndex > maxIndex || fromNodeIndex < 0) return Result.failure(IndexOutOfBoundsException("fromNodeIndex $fromNodeIndex out of bounds."))
        if (toNodeIndex > maxIndex || toNodeIndex < 0) return Result.failure(IndexOutOfBoundsException("toNodeIndex $toNodeIndex out of bounds."))
        val result = this.adjacencyList[fromNodeIndex].contains(toNodeIndex)
        return Result.success(result)
    }

    /**
     * Get the edges connecting two nodes, by node indexes.
     *
     * @return a `Result` containing a list of edges or failure if the node indexes is out of bounds.
     */
    fun edgesBetween(fromNodeIndex: Int, toNodeIndex: Int): Result<List<E>> {
        val result = mutableListOf<E>()
        val maxIndex = this._nodes.lastIndex
        if (fromNodeIndex > maxIndex || fromNodeIndex < 0) return Result.failure(IndexOutOfBoundsException("fromNodeIndex $fromNodeIndex out of bounds."))
        if (toNodeIndex > maxIndex || toNodeIndex < 0) return Result.failure(IndexOutOfBoundsException("toNodeIndex $toNodeIndex out of bounds."))
        for ((index, adjacency) in adjacencyList[fromNodeIndex].withIndex()) {
            if (adjacency == toNodeIndex) {
                result.add(_edges[fromNodeIndex][index])
            }
        }
        return Result.success(result.toList())
    }

    override fun edgesBetween(fromNode: N, toNode: N): Result<List<E>> {
        val fromNodeIndex = getNodeIndex(fromNode)
        val toNodeIndex = getNodeIndex(toNode)
        if (fromNodeIndex == -1 || toNodeIndex == -1) return Result.failure(IllegalArgumentException("Node could not be found in the graph."))
        return edgesBetween(fromNodeIndex, toNodeIndex)
    }

    /**
     * Get the edges from a node specified by its index to other nodes.
     *
     * @return a `Result` containing a list of edges and the node indexes they point to or a failure if the index is out
     * of bounds.
     */
    fun edgesFrom(index: Int): Result<List<Pair<E, Int>>> {
        val maxIndex = this._nodes.lastIndex
        if (index > maxIndex || index < 0) return Result.failure(IndexOutOfBoundsException("index $index out of bounds."))
        val result = _edges[index].zip(adjacencyList[index])
        return Result.success(result.toList())
    }

    override fun edgesFrom(node: N): Result<List<Pair<E, N>>> {
        val fromNodeIndex = getNodeIndex(node)
        if (fromNodeIndex == -1) return Result.failure(IllegalArgumentException("Node could not be found in the graph."))
        val ef = edgesFrom(fromNodeIndex)
        ef.onFailure { return Result.failure(it) }
        val result = (ef.getOrNull() ?: listOf()).map { it.first to _nodes[it.second] }.toList()
        return Result.success(result)
    }

    /**
     * Get the edges to a node specified by its index from other nodes.
     *
     * @return a `Result` containing a list of edges and the nodes indexes they point from or a failure if the index is
     * out of bounds.
     */
    fun edgesTo(index: Int): Result<List<Pair<E, Int>>> {
        val maxIndex = this._nodes.lastIndex
        if (index > maxIndex || index < 0) return Result.failure(IndexOutOfBoundsException("index $index out of bounds."))
        val result = mutableListOf<Pair<E, Int>>()
        for ((i, e) in adjacencyList.withIndex()) {
            if (e.contains(index)) result.add(_edges[i][e.indexOf(index)] to i)
        }
        return Result.success(result.toList())
    }

    override fun edgesTo(node: N): Result<List<Pair<E, N>>> {
        val fromNodeIndex = getNodeIndex(node)
        if (fromNodeIndex == -1) return Result.failure(IllegalArgumentException("Node could not be found in the graph."))
        val result = (edgesTo(fromNodeIndex).onFailure { return Result.failure(it) }.getOrNull()
            ?: emptyList()).map { it.first to _nodes[it.second] }
        return Result.success(result)
    }

    override fun childNodes(node: N): Result<List<N>> {
        val fromNodeIndex = getNodeIndex(node)
        if (fromNodeIndex == -1) return Result.failure(IllegalArgumentException("Node could not be found in the graph."))
        val result = this.adjacencyList[fromNodeIndex].map { this._nodes[it] }
        return Result.success(result)
    }

    /**
     * Get the adjacent child node indexes of a node specified by its index.
     *
     * @return a `Result` containing a list of node indexes or error if the node index is out of bounds.
     */
    fun childNodes(index: Int): Result<List<Int>> {
        val maxIndex = this._nodes.lastIndex
        if (index > maxIndex || index < 0) return Result.failure(IndexOutOfBoundsException("index $index out of bounds."))
        val result = this.adjacencyList[index].map { it }
        return Result.success(result)
    }

    /**
     * Get all the descendant nodes of a node.
     *
     * @return a `Result` containing a list of node indexes or error if the node index is out of bounds.
     */
    fun descendants(node: N): Result<List<N>> {
        val fromNodeIndex = getNodeIndex(node)
        if (fromNodeIndex == -1) return Result.failure(IllegalArgumentException("Node could not be found in the graph."))
        val result = descendants(fromNodeIndex).getOrNull() ?: emptyList()
        return Result.success(result.map { _nodes[it] })
    }

    /**
     * Get all the descendant nodes of a node specified by its index.
     *
     * @return a `Result` containing a list of node indexes or error if the node index is out of bounds.
     */
    fun descendants(index: Int): Result<List<Int>> {
        val maxIndex = this._nodes.lastIndex
        if (index > maxIndex || index < 0) return Result.failure(IndexOutOfBoundsException("index $index out of bounds."))
        val result = mutableListOf<Int>()
        val queue = (childNodes(index).getOrNull() ?: emptyList()).toMutableList()
        while (queue.isNotEmpty()) {
            val candidate = queue.removeFirst()
            if (!result.contains(candidate)) result.add(candidate)
            queue.addAll(childNodes(candidate).getOrNull() ?: emptyList())
        }
        return Result.success(result)
    }

    override fun parentNodes(node: N): Result<List<N>> {
        val fromNodeIndex = getNodeIndex(node)
        if (fromNodeIndex == -1) return Result.failure(IllegalArgumentException("Node could not be found in the graph."))
        val plist = parentNodes(fromNodeIndex)
        if (plist.isFailure) return Result.failure(plist.exceptionOrNull() ?: Exception(""))
        val result = (plist.getOrNull() ?: emptyList()).map { _nodes[it] }
        return Result.success(result)
    }

    /**
     * Get the parent node indexes of a node specified by its index.
     *
     * @return a `Result` containing a list of node indexes or error if the node index is out of bounds.
     */
    fun parentNodes(index: Int): Result<List<Int>> {
        val maxIndex = this._nodes.lastIndex
        if (index > maxIndex || index < 0) return Result.failure(IndexOutOfBoundsException("index $index out of bounds."))
        val result = mutableListOf<Int>()
        for ((i, e) in adjacencyList.withIndex()) {
            if (e.contains(index)) result.add(i)
        }
        return Result.success(result.toList())
    }
}

/**
 * A node indexer to speed up node lookups in large graphs, using binary search instead of default sequential
 * search in methods of the Graph class.
 */
class NodeIndexer<N : Comparable<N>>(nodes: List<N>) {
    private var sortedNodes = listOf<Pair<Int, N>>()

    init {
        sortedNodes = (0..nodes.lastIndex).zip(nodes).sortedBy { it.second }
    }

    /**
     * Get the index of a node utilising a binary search through nodes.
     */
    fun indexOf(node: N): Int {
        val lookupIndex = sortedNodes.binarySearchBy(node) { it.second }
        return if (lookupIndex < 0) -1 else sortedNodes[lookupIndex].first
    }
}

/**
 * A directed graph with node indexing functionality to speed up node lookups.
 *
 * Inherits from UnIndexedGraph and utilises NodeIndexer. To be able to be indexed, nodes must meet the
 * Comparable type constraint. The getNode method is overridden to use the indexer to find nodes,
 * and node addition methods also to reset the indexer to null when nodes are changed.
 *
 * Once nodes have been added the indexNodes method must be called to create the node index
 * (otherwise reverts to sequential lookup).
 *
 * @param N the type of graph nodes
 * @param E the type of graph edges
 */
class NodeIndexedGraph<N : Comparable<N>, E> : UnIndexedGraph<N, E>(), Graph<N, E> {
    private var nodeIndexer: NodeIndexer<N>? = null

    val isNodeIndexed: Boolean get() = nodeIndexer != null

    override fun toString(): String = "NodeIndexedGraph[order=${this.order}, size=${this.size}, indexed=$isNodeIndexed]"

    /**
     * Update the NodeIndexer, which sorts nodes in an index to use binary search lookups.
     *
     * Once indexed getNodeIndex will use the NodeIndexer to find a node index instead of the
     * inbuilt sequential node search in the underlying Graph class.
     *
     * This method needs to be called after every change to nodes.
     */
    fun indexNodes() {
        // See getNodeIndex for where this comes to life
        nodeIndexer = NodeIndexer(nodes)
    }

    override fun addNode(node: N): Result<Unit> {
        val superResult = super.addNode(node)
        if (superResult.isFailure) return superResult
        nodeIndexer = null
        return Result.success(Unit)
    }

    override fun addNodes(vararg nodes: N): Result<Unit> {
        val superResult = super.addNodes(nodes.toList())
        if (superResult.isFailure) return superResult
        nodeIndexer = null
        return Result.success(Unit)
    }

    override fun addNodes(nodes: List<N>): Result<Unit> {
        val superResult = super.addNodes(nodes)
        if (superResult.isFailure) return superResult
        nodeIndexer = null
        return Result.success(Unit)
    }

    override fun setNode(nodeIndex: Int, node: N): Result<Unit> {
        val superResult = super.setNode(nodeIndex, node)
        if (superResult.isFailure) return superResult
        nodeIndexer = null
        return Result.success(Unit)
    }

    override fun setNodes(nodes: Set<N>) {
        super.setNodes(nodes)
        nodeIndexer = null
    }

    override fun getNodeIndex(node: N): Int {
        return if (nodeIndexer != null) nodeIndexer!!.indexOf(node)
        else this._nodes.indexOf(node)
    }
}

/**
 * A directed graph with a map used to look up node indexes, instead of sequential lookup in the
 * base class.
 *
 * @param N the type of graph nodes
 * @param E the type of graph edges
 */
class NodeMappedGraph<N, E> : UnIndexedGraph<N, E>(), Graph<N, E> {
    private var nodeMap = mutableMapOf<N, Int>()

    override fun toString(): String = "NodeMappedGraph[order=${this.order}, size=${this.size}]"

    override fun addNode(node: N): Result<Unit> {
        val nextIndex = order
        val superResult = super.addNode(node)
        if (superResult.isFailure) return superResult
        nodeMap[node] = nextIndex
        return Result.success(Unit)
    }

    override fun addNodes(vararg nodes: N): Result<Unit> {
        val nextIndex = order
        val superResult = super.addNodes(nodes.toList())
        if (superResult.isFailure) return superResult
        for ((index, node) in nodes.withIndex()) {
            nodeMap[node] = nextIndex + index
        }
        return Result.success(Unit)
    }

    override fun addNodes(nodes: List<N>): Result<Unit> {
        val nextIndex = order
        val superResult = super.addNodes(nodes)
        if (superResult.isFailure) return superResult
        for ((index, node) in nodes.withIndex()) {
            nodeMap[node] = nextIndex + index
        }
        return Result.success(Unit)
    }

    override fun setNode(nodeIndex: Int, node: N): Result<Unit> {
        val superResult = super.setNode(nodeIndex, node)
        if (superResult.isFailure) return superResult
        nodeMap.remove(node)
        nodeMap[node] = nodeIndex
        return Result.success(Unit)
    }

    override fun setNodes(nodes: Set<N>) {
        super.setNodes(nodes)
        for ((index, node) in nodes.withIndex()) {
            nodeMap[node] = index
        }
    }

    override fun getNodeIndex(node: N): Int = nodeMap.getOrDefault(node, -1)

    /**
     * Find the shortest path between nodes using the A* algorithm, taking into account edge weights.
     *
     * The effectiveness of this method depends on the ability to define an appropriate heuristic function `h`. The heuristic
     * is used to reduce the number of nodes visited during path search and therefore an inappropriate `h` can result in
     * the path found not being the true shortest path.
     *
     * Implemented in `GraphNodeMapped` given the implementation uses a node Map and therefore the same
     * type constraints apply (i.e. `N` is a mappable type).
     *
     * See [Wikipedia](https://en.wikipedia.org/wiki/A*_search_algorithm).
     * @param fromNode start of the path
     * @param toNode target node for the path to end on
     * @param getDistance a function to extract a distance from an edge and return a Double value. This enables
     * typesafe handling of different types of edges.
     * @param h a heuristic function, with h(n) estimating the cost to reach the goal node from node n
     * @return a `Result` containing a list of node indexes representing the shortest path or a failure if indexes are out
     * of bounds or if a path could not be found.
     */
    fun shortestPathAStar(fromNode: N, toNode: N, getDistance: (E) -> Double, h: (N) -> Double): Result<List<N>> {
        if (!hasNode(fromNode) || !hasNode(toNode)) return Result.failure(IllegalArgumentException("Node could not be found in the graph."))
        // The set of discovered nodes that may need to be (re-)expanded. Initially, only the start node is known.
        val openSet = mutableListOf<N>()
        openSet.add(fromNode)
        // For node n, cameFrom[n] is the node immediately preceding it on the cheapest path from the start to n
        // currently known.
        val cameFrom = mutableMapOf<N, N>()
        // For node n, gScore[n] is the cost of the cheapest path from start to n currently known. The default value
        // is infinity.
        val gScore = mutableMapOf<N, Double>()
        gScore[fromNode] = 0.0
        // For node n, fScore[n] = gScore[n] + h(n). fScore[n] represents our current best guess as to
        // how cheap a path could be from start to finish if it goes through n.
        val fScore = mutableMapOf<N, Double>()
        fScore[fromNode] = h(fromNode)
        while (openSet.isNotEmpty()) {
            // The node in openSet having the lowest fScore[] value
            var current = openSet.map { it to fScore[it]!! }.minBy { it.second }.first
            if (current == toNode) {
                val path = mutableListOf(current)
                while (current in cameFrom.keys) {
                    current = cameFrom[current]!!
                    path.addFirst(current)
                }
                return Result.success(path.toList())
            }
            openSet.remove(current)
            val children = childNodes(current).getOrNull() ?: listOf()
            for (child in children) {
                // d(current,neighbor) is the weight of the edge from current to neighbor
                // tentative_gScore is the distance from start to the neighbor through current:
                // tentative_gScore := gScore[current] + d(current, neighbor)
                val eb = (edgesBetween(current, child).getOrNull() ?: listOf()).first()
                val tentativeGScore = gScore[current]!! + getDistance(eb)
                if (tentativeGScore < gScore.getOrDefault(child, Double.MAX_VALUE)) {
                    // This path to neighbor is better than any previous one. Record it.
                    cameFrom[child] = current
                    gScore[child] = tentativeGScore
                    fScore[child] = tentativeGScore + h(child)
                    if (!openSet.contains(child)) openSet.add(child)
                }
            }
        }
        return Result.failure(NoSuchElementException("A path could not be found"))
    }
}

/**
 * A path tree containing the paths from a source node. Paths can split and rejoin but can not loop back
 * to a previous split.
 *
 * @param sourceNode the start of the path tree
 * @graph the underlying graph object containing the path tree. Defaults to `Graph`. Enables utilising
 * the same underlying graph type when building a path tree from a graph.
 */
class PathTree<N>(val sourceNode: N, graph: Graph<N, Double> = UnIndexedGraph()) {
    private var graph: Graph<N, Double> = UnIndexedGraph()
    private val distances = mutableListOf<Double>()

    init {
        this.graph = graph
        addPathNode(sourceNode)
    }

    val nodes: List<N> get() = graph.nodes

    /**
     * Build a path tree between two nodes.
     *
     * @param sourceGraph the graph to find paths in
     * @param fromNode the node to start searching from
     * @param toNode the node to end with
     * @param maxSteps the maximum number of steps to create the path tree
     * @param getDistance a function to extract a distance from an edge and return a Double value. This enables
     * typesafe handling of different types of edges.
     * @param treeGraph a Graph to use in the path tree
     * @return a `Result` `PathTree` containing paths between the nodes on success or failure if nodes could not be
     * found in the graph.
     */
    fun <E> buildPathTree(
        sourceGraph: Graph<N, E>,
        fromNode: N,
        toNode: N,
        maxSteps: Int,
        getDistance: (E) -> Double,
        treeGraph: Graph<N, Double> = UnIndexedGraph()
    ): Result<Unit> {
        val fromNodeIndex = sourceGraph.getNodeIndex(fromNode)
        val toNodeIndex = sourceGraph.getNodeIndex(toNode)
        if (fromNodeIndex == -1 || toNodeIndex == -1) return Result.failure(IllegalArgumentException("Node could not be found in the graph."))
        this.graph = treeGraph
        addPathNode(fromNode)
        var nodesToProcess = listOf(fromNode)
        val blockedEndNodes = mutableListOf<N>()
        // To avoid rejoining a previous split causing an infinite loop
        val nodesPreviouslySplit = mutableListOf<N>()
        var steps = 0
        while (true) {
            steps += 1
            if (steps > maxSteps) return Result.failure(IndexOutOfBoundsException("Maximum number of steps exceeded"))
            for (endNode in nodesToProcess) {
                if (endNode == toNode) continue
                val children = (sourceGraph.childNodes(endNode).getOrNull() ?: listOf()).filter {
                    !blockedEndNodes.contains(it)
                }
                if (children.isEmpty() && endNode != toNode) {
                    blockedEndNodes.add(endNode)
                    backtrackToLastSplit(endNode)
                    continue
                }
                if (children.count() > 1) nodesPreviouslySplit.add(endNode)
                for (child in children) {
                    if (child in nodesPreviouslySplit) {
                        blockedEndNodes.add(endNode)
                        backtrackToLastSplit(endNode)
                        continue
                    }
                    val edges = sourceGraph.edgesBetween(endNode, child).getOrNull() ?: listOf()
                    addStep(endNode, child, getDistance(edges.first()))
                }
            }
            nodesToProcess = this.endNodes
            if (nodesToProcess.isEmpty() || nodesToProcess.all { it == toNode }) break
        }
        return Result.success(Unit)
    }

    /**
     * The distance of nodes from the start.
     */
    val nodesDistanceFromStart: List<Double> get() = distances.toList()

    /**
     * The nodes in the path that don't have any child nodes, or child nodes do not have weights of 0,
     * or do have a parent node, or parent step weights are not 0, and are not the start node.
     */
    val endNodes: List<N>
        get() = graph.nodes.filter {
            val childWeightsZero = (graph.childNodes(it).getOrNull() ?: listOf()).sumOf { child ->
                (graph.edgesBetween(it, child).getOrNull() ?: listOf()).sum()
            } == 0.0
            val parentWeightsNotZero = (graph.parentNodes(it).getOrNull() ?: listOf()).sumOf { parent ->
                (graph.edgesBetween(parent, it).getOrNull() ?: listOf()).sum()
            } != 0.0
            childWeightsZero && parentWeightsNotZero && it != sourceNode
        }

    /**
     * The distance of a node from the start.
     */
    fun nodeDistanceFromStart(node: N): Result<Double> {
        val nodeIndex = nodes.indexOf(node)
        if (nodeIndex == -1) return Result.failure(IllegalArgumentException("node $node not in the path"))
        return Result.success(distances[nodeIndex])
    }

    private fun addPathNode(node: N) {
        graph.addNode(node)
        if (node == sourceNode) distances.add(0.0)
        else distances.add(Double.MAX_VALUE)
    }

    /**
     * Add a step to a node. The fromNode must already exist in the path tree.
     *
     * @return a `Result` containing a Unit on success or failure if the fromNode is not already in the path tree.
     */
    fun addStep(fromNode: N, toNode: N, stepWeight: Double = 1.0): Result<Unit> {
        if (!graph.hasNode(fromNode)) return Result.failure(IllegalArgumentException("fromNode $fromNode must already be in the path"))
        if (!graph.hasNode(toNode)) {
            addPathNode(toNode)
        }
        // Create the edge connection
        graph.addEdge(fromNode = fromNode, toNode = toNode, edge = stepWeight)
        // Update the cumulative distance
        val fromNodeCumulativeDistance = distances[graph.nodes.indexOf(fromNode)]
        val toNodeIndex = graph.nodes.indexOf(toNode)
        // Check if there are other parents, and take the lowest cost
        val toNodeParents = graph.parentNodes(toNode).getOrNull() ?: listOf()
        if (toNodeParents.count() == 1) {
            distances[toNodeIndex] = fromNodeCumulativeDistance + stepWeight
        } else {
            val toNodeParentsDistances = toNodeParents.map { distances[graph.getNodeIndex(it)] }
            distances[toNodeIndex] = toNodeParentsDistances.min() + stepWeight
        }
        return Result.success(Unit)
    }

    /**
     * Remove all steps from an end node, back to the last split in the tree.
     */
    fun backtrackToLastSplit(node: N): Result<Unit> {
        if (!graph.hasNode(node)) return Result.failure(IllegalArgumentException("node $node not found in the path"))
        val children = graph.childNodes(node).getOrNull() ?: listOf()
        // Note a child could exist but with an edge between parent and child with zero weight.
        // This is treated as non-existing step. See removeStep which sets edges to zero.
        val childEdgeWeights = children.sumOf { child ->
            val edges = graph.edgesBetween(node, child).getOrNull() ?: listOf()
            edges.sumOf { it }
        }
        if (childEdgeWeights != 0.0) return Result.failure(IllegalArgumentException("backtracking only allowed from an end node"))
        var currentNode = node
        while (true) {
            val parents = graph.parentNodes(currentNode).getOrNull() ?: listOf()
            if (parents.isEmpty()) break
            if (parents.count() > 1) break
            val parentNode = parents.first()
            val parentChildren = graph.childNodes(parentNode).getOrNull() ?: listOf()
            if (parentChildren.count() > 1) {
                removeStep(parentNode, currentNode)
                break
            } else {
                removeStep(parentNode, currentNode)
                currentNode = parentNode
            }
            if (currentNode == sourceNode) break
        }
        return Result.success(Unit)
    }

    /**
     * Enumerate paths encoded in the path tree into separate paths.
     *
     * @return a `Result` containing enumerated paths as node lists or failure if the path tree doesn't have
     * any end nodes e.g. only contains the start node or contains a loop.
     */
    fun enumeratePaths(): Result<List<List<N>>> {
        val ends = endNodes
        if (ends.isEmpty()) return Result.failure(IllegalStateException("No end nodes"))
        var result = mutableListOf<MutableList<N>>()
        for (endNode in ends) result.add(mutableListOf(endNode))
        while (true) {
            val newPaths = mutableListOf<MutableList<N>>()
            val inValidPaths = mutableListOf<Int>()
            for ((pathIndex, path) in result.withIndex()) {
                val currentNode = path.last()
                if (currentNode == sourceNode) continue
                val parents = (graph.parentNodes(currentNode).getOrNull() ?: listOf()).filter { parent ->
                    val edges = graph.edgesBetween(parent, currentNode).getOrNull() ?: listOf()
                    edges.sumOf { it } > 0.0
                }
                if (parents.isEmpty()) {
                    inValidPaths.add(pathIndex)
                    continue
                }
                val pathBefore = path.toMutableList()
                path.add(parents.first())
                if (parents.count() > 1) {
                    for (parentIndex in (1..parents.lastIndex)) {
                        val newPath = pathBefore.toMutableList()
                        newPath.add(parents[parentIndex]!!)
                        newPaths.add(newPath)
                    }
                }
            }
            result =
                result.withIndex().filter { (i, _) -> !inValidPaths.contains(i) }.map { (_, e) -> e }.toMutableList()
            result += newPaths
            if (result.isEmpty()) break
            if (result.all { it.last() == sourceNode }) break
        }
        return Result.success(result.map { it.reversed() })
    }

    /**
     * Remove a step link and subsequent step links from the path tree, up to the next path convergence
     * by setting the step weight to 0.0.
     *
     * Step nodes are not deleted. The toNode distance from start is set to maximum value for the
     * toNode and its child nodes.
     *
     * @return a `Result` `Unit` or failure if the nodes are not in the path tree.
     */
    fun removeStep(fromNode: N, toNode: N): Result<Unit> {
        if (!graph.hasNode(fromNode)) return Result.failure(IllegalArgumentException("fromNode $fromNode not found in the path"))
        if (!graph.hasNode(toNode)) return Result.failure(IllegalArgumentException("toNode $toNode not found in the path"))
        graph.updateEdge(fromNode, toNode, 0.0)
        val toNodeIndex = graph.nodes.indexOf(toNode)
        distances[toNodeIndex] = Double.MAX_VALUE
        for (childNode in graph.childNodes(toNode).getOrNull() ?: listOf()) {
            val childNodeParents = graph.parentNodes(childNode).getOrNull() ?: listOf()
            if (childNodeParents.count() > 1) break
            removeStep(toNode, childNode)
        }
        return Result.success(Unit)
    }
}