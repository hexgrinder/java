/**
 * 
 */
package hexgrinder.search;

import java.util.ArrayList;

/**
 * @author hexgrinder
 *
 */
public class QuadTreeNodes {
	
	private static final int QT_MAX_DEFAULT_NODE_CAPACITY = 1;
	
	private int _maxNodeCapacity = QT_MAX_DEFAULT_NODE_CAPACITY;
	private ArrayList<QuadTreeNode> _nodes = new ArrayList<QuadTreeNode>(100);
	
	public boolean showAllQuads;
	
	/**
	 * QuadTreeNodes constructor.
	 * 
	 * Initializes the collection.  The tree will be initialized with a root as
	 * defined by the upper-left and lower-right bounding box.
	 * 
	 * @param upperLeft Upper-left bounds of the root node
	 * @param lowerRight Lower-right bounds of the root node
	 * @param maxNodeCapacity Maximum capacity of each node
	 */
	public QuadTreeNodes(Coordinate upperLeft, Coordinate lowerRight, int maxNodeCapacity) {
		this._maxNodeCapacity = maxNodeCapacity;
		// Add the root node
		_nodes.add(new QuadTreeNode(upperLeft, lowerRight));
		this.showAllQuads = false;
	}
	
	
	
	/**
	 * Inserts a coordinate into the collection.
	 * 
	 * @param point Coordinate to insert.
	 * @param level Max level of recursion
	 * @return Returns TRUE if coordinate was inserted, FALSE otherwise.
	 */
	public boolean insert(Coordinate point, int level) {
		// Start at root and then trickle down
		return _insert(0, point, level);
	}
	
	/**
	 * Returns the node at the given index.
	 * 
	 * @param index Index of node to return
	 * @return Returns the node at the given index
	 */
	public QuadTreeNode get(int index) {
		return _nodes.get(index);
	}
	
	public Integer[] getBoxes() {
	
		ArrayList<Integer> rtn = new ArrayList<Integer>();
		Integer[] boxCoordinates = {};
		
		
		for (QuadTreeNode node : _nodes) {
			if (this.showAllQuads 
				|| (node.isLeaf && !node.isEmpty)) {
				rtn.add(node.boundary.getUpperLeft().getX());
				rtn.add(node.boundary.getUpperLeft().getY());
				rtn.add(node.boundary.getLowerRight().getX());
				rtn.add(node.boundary.getLowerRight().getY());
			}
		}
		
		return rtn.toArray(boxCoordinates);
	}
	
	public Integer[] getCenters() {
		ArrayList<Integer> rtn = new ArrayList<Integer>();
		Integer[] t = {};
		int[] temp;
		for (QuadTreeNode node : _nodes) {
			//if (node.isLeaf && !node.isEmpty) {
				temp = node.boundary.getCenter();
				rtn.add(temp[0]);
				rtn.add(temp[1]);
			//}
		}
		temp = null;
		return rtn.toArray(t);
	}
	
	/**
	 * Returns the number of nodes in the collection.
	 *
	 * @return Returns the number of nodes in the collection.
	 */
	public int size() {
		return _nodes.size();
	}
	
	/**
	 * Clears the collection of all nodes.
	 */
	public void clear() {
		_nodes.clear();
	}
	
	// ***** HELPER FUNCTIONS *****
	
	/**
	 * Recursive insert algorithm
	 * 
	 * @param nodeIndex Node index where the to start the insert operation.
	 * @param point Coordinate to insert.
	 * @param level Max level of recursion.
	 * @return Returns TRUE if coordinate was inserted, FALSE otherwise.
	*/
	private boolean _insert(int nodeIndex, Coordinate point, int level) {
		
		// TODO? HOW TO ADD PREVIOUSLY ENCOUNTERED ELEMENTS IN MATRIX
		// ALTER INSERT TO NEXT RECURSIVE LEVEL THEN ZERO OUT PARENT?
		QuadTreeNode node = _nodes.get(nodeIndex);
		
		
		// If level is reached or the point is not within 
		// the boundary, kick out.
		if (!node.boundary.isInBoundary(point)) { 
			return false;
		} 
		
		// Flag that indicates a node has been added
		node.isEmpty = false;
		
		// point added
		++node.elemCount;
		
		// Threshold gate keeper
		if (node.elemCount <= _maxNodeCapacity) { 
			// append index to list of indexes
			
			//node.add(point);
			return true; 
		}
		
		// no need to build out children if we will reach 
		// zero-level on the next recursive step.
		if (level-1 <= 0) { 
			//node.add(point);
			return true; 
		}
		
		// I am now a parent!!!
		node.isLeaf = false;	// No longer a leaf
		node.isEmpty = true;	// Inner nodes do not have contents
		
		
		// Make childrens!!!
		
		// subdivide and recursive search
		if (!node.hasChildren()) {
			_subDivide(nodeIndex);
		}
		
		// TODO? TAKE CURRENT LIST AND REDISTRIBUTE AMONGST KIDS? 
		// CURRENT NODE LIST IS EMPTY AT POST?
		//Coordinate pt;
		//while (!node.isEmpty()) {
			//pt = node.remove(0);
			// Recursive search of new kids
			if (_insert(node.children[QuadTreeNodeType.NW_NODE.ordinal()], point, level-1)) { 
				return true;
				//continue
			}
			if (_insert(node.children[QuadTreeNodeType.NE_NODE.ordinal()], point, level-1)) { 
				return true;
				//continue; 
			}
			if (_insert(node.children[QuadTreeNodeType.SE_NODE.ordinal()], point, level-1)) { 
				return true;
				//continue; 
			}
			if (_insert(node.children[QuadTreeNodeType.SW_NODE.ordinal()], point, level-1)) { 
				return true;
				//continue; 
			}
		//}
		
		// Cannot add element
		return false;
	}
	
	/**
	 * Subdivides the parent node into 4 children.  
	 * 
	 * The parent's children array will contain indexes to its children.
	 * The 4 children will have the following boundaries:
	  
	 		NW child (index 0)
				upperLeft 
					parent.boundary.upperLeft.X
					parent.boundary.upperLeft.Y 
				lowerRight
					parent.boundary.lowerRight.X / 2 (use right shift)
					parent.boundary.lowerRight.Y / 2 (use right shift)
			NE child (index 1)
				upperLeft
					parent.boundary.lowerRight.X / 2 (use right shift)
					parent.boundary.upperRight.Y 
				lowerRight
					parent.boundary.lowerRight.X
					parent.boundary.lowerRight.Y / 2 (use right shift)
			SE child (index 2)
				upperLeft
					parent.boundary.lowerRight.X / 2 (use right shift)
					parent.boundary.lowerRight.Y / 2 (use right shift)
				lowerRight
					parent.boundary.lowerRight.X
					parent.boundary.lowerRight.Y
			SW child (index 3)
				upperLeft
					parent.boundary.upperLeft.X
					parent.boundary.lowerRight.Y / 2 (use right shift)
				lowerRight
					parent.boundary.lowerRight.X / 2 (use right shift)
					parent.boundary.lowerRight.Y
		
	 * @param nodeIndex Index of the node to divide
	 */
	private void _subDivide(int nodeIndex) {

		QuadTreeNode node = _nodes.get(nodeIndex);
		
		// calc boundaries
		int origin_X = node.boundary.getUpperLeft().getX();
		int origin_Y = node.boundary.getUpperLeft().getY();
		int bound_X = node.boundary.getLowerRight().getX();
		int bound_Y = node.boundary.getLowerRight().getY();
		int half_X = origin_X + ((bound_X - origin_X) >> 1);	// divide by 2
		int half_Y = origin_Y + ((bound_Y - origin_Y) >> 1);  // divide by 2
		
		// create the 4 children
		
		// NW
		_addNewNode(
			node,
			QuadTreeNodeType.NW_NODE,
			new Coordinate(origin_X, origin_Y),
			new Coordinate(half_X, half_Y));

		// NE
		_addNewNode(
			node,
			QuadTreeNodeType.NE_NODE,
			new Coordinate(half_X+1, origin_Y),
			new Coordinate(bound_X, half_Y));
		
		// SE
		_addNewNode(
			node,
			QuadTreeNodeType.SE_NODE,
			new Coordinate(half_X+1, half_Y+1),
			new Coordinate(bound_X, bound_Y));
		
		// SW
		_addNewNode(
			node,
			QuadTreeNodeType.SW_NODE,
			new Coordinate(origin_X, half_Y+1),
			new Coordinate(half_X, bound_Y));
		
	}
	
	/**
	 * Adds a new child to the array and maps the children's indexes
	 * to the parents children property.
	 *  
	 * @param parent Parent node
	 * @param qtnode QuadTree.Node type
	 * @param upperLeft Upper-left boundary of the new child
	 * @param lowerRight Lower-right boundary of the new child
	 */
	private void _addNewNode(QuadTreeNode parent, QuadTreeNodeType qtnode, Coordinate upperLeft, Coordinate lowerRight) {
		QuadTreeNode newElem = null;
		newElem = new QuadTreeNode(
			upperLeft,
			lowerRight);
		_nodes.add(newElem);
		parent.children[qtnode.ordinal()] = _nodes.lastIndexOf(newElem);
	}
	
} // QuadTreeNodes
