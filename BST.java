import java.io.*;
import org.junit.*;
import static org.junit.Assert.*;
// Download junit http://junit.org/junit4/faq.html
// set CLASSPATH or copy the *.jar files to /Library/Java/Extentions
// java org.junit.runner.JUnitCore BST

public class BST<T> {
  // TreeNode defines a data structure internal to BST that contains
  // a tree node. It works for any type T as long as toString() is
  // defined for it.
  private class TreeNode<T> {
    private int id;
    private T data;
    private TreeNode<T> leftchild;
    private TreeNode<T> rightchild;
    private int height;

    public TreeNode(int id, T data) {
      this.id = id;
      this.data = data;
      this.leftchild = null;
      this.rightchild = null;
      this.height = 0;
    }
  } // end class TreeNode

  private TreeNode<T> root;

  public BST() {
    this.root = null;
  }

  public String toString() {
    return size() + ":" + toString(this.root);
  }

  private String toString(TreeNode<T> node) {
    if (node == null) {
      return "[]";
    }
    String out = "[";
    out += toString(node.leftchild);
    out += node.id;
    out += toString(node.rightchild);
    out += "]";
    return out;
  }

  private int height(TreeNode<T> node) {
    return node == null ? -1 : node.height;
  }

  public int size() {
    return size(this.root);
  }

  private int size(TreeNode<T> root) {
    if (root == null)
      return 0;
    return size(root.leftchild) + size(root.rightchild) + 1;
  }

  public TreeNode<T> find(int id) {
    return find(this.root, id);
  }

  private TreeNode<T> find(TreeNode<T> root, int id) {
    if (root == null) {
      return null;
    } else if (root.id == id) {
      return root;
    } else if (id < root.id) {
      return find(root.leftchild, id);
    } else {
      return find(root.rightchild, id);
    }
  }

  public void add(int id, T data) {
    this.root = insert(this.root, id, data);
  }

  private TreeNode<T> insert(TreeNode<T> root, int id, T data) {
      if (root == null) {
          root = new TreeNode<T>(id, data);
      } else if (id == root.id) {
        ; // error!
      } else if (id < root.id) {
          root.leftchild = insert(root.leftchild, id, data);
          if( height(root.leftchild) - height(root.rightchild) == 2) {
              if(id < root.leftchild.id )
                  root = rotateWithLeftChild(root);
              else
                  root = doubleWithLeftChild(root);
          }
      } else if (id > root.id) {
          root.rightchild = insert(root.rightchild, id, data);
          if(height(root.rightchild) - height(root.leftchild) == 2) {
              if(id > root.rightchild.id)
                  root = rotateWithRightChild(root);
              else
                  root = doubleWithRightChild(root);
          }
      }
      root.height = Math.max(height(root.leftchild), height(root.rightchild)) + 1;
      return root;
  }

  // Rotate binary tree node with left child.
  private TreeNode<T> rotateWithLeftChild(TreeNode<T> k2) {
      TreeNode<T> k1 = k2.leftchild;
      k2.leftchild = k1.rightchild;
      k1.rightchild = k2;
      k2.height = Math.max(height(k2.leftchild), height( k2.rightchild)) + 1;
      k1.height = Math.max(height(k1.leftchild), k2.height) + 1;
      return k1;
  }

  // Rotate binary tree node with right child
  private TreeNode<T> rotateWithRightChild(TreeNode<T> k1) {
      TreeNode<T> k2 = k1.rightchild;
      k1.rightchild = k2.leftchild;
      k2.leftchild = k1;
      k1.height = Math.max( height(k1.leftchild), height( k1.rightchild)) + 1;
      k2.height = Math.max( height(k2.rightchild), k1.height) + 1;
      return k2;
  }
  // Double rotate binary tree node: first left child
  // with its right child; then node k3 with new left child
  private TreeNode<T> doubleWithLeftChild(TreeNode<T> k3) {
      k3.leftchild = rotateWithRightChild(k3.leftchild);
      return rotateWithLeftChild(k3);
  }

  // Double rotate binary tree node: first right child
  // with its left child; then node k1 with new right child
  private TreeNode<T> doubleWithRightChild(TreeNode<T> k1) {
      k1.rightchild = rotateWithLeftChild(k1.rightchild);
      return rotateWithRightChild(k1);
  }

  public void remove(int id) {
    if (find(this.root, id) == null) {
      // The element is no where to be found. We're done.
      // TODO consider throwing an exception, thought it can be
      // considerd bad form.
      return;
    }
    this.root = removeNodeWithId(this.root, id);
  }

  private TreeNode<T> findSmallest(TreeNode<T> root) {
    TreeNode<T> node = root;
    while (node.leftchild != null)
      node = node.leftchild;
    return node;
  }

  private TreeNode<T> removeNodeWithId(TreeNode<T> root, int id) {
    if (root == null) {
      return root;
    }
    if (id < root.id) {
      root.leftchild = removeNodeWithId(root.leftchild, id);
    } else if (root.id < id) {
      root.rightchild = removeNodeWithId(root.rightchild, id);
    } else {
      // Delete this node.
      // First handle cases when there is one or zero children.
      if (root.leftchild == null) {
        return root.rightchild;
      } else if (root.rightchild == null) {
        return root.leftchild;
      } else {
        // Node with two children. Get the inorder successor (smallest
        // in the right subtree), copy into this node, then
        // delete the successor.
        TreeNode<T> next_smallest = findSmallest(root.rightchild);
        root.id = next_smallest.id;
        root.data = next_smallest.data;
        root.rightchild = removeNodeWithId(root.rightchild, root.id);
      }
    }
    return root;
  }

  // Verifies that the tree is (a) valid BST and (b) balanced.
  public boolean verifyBalancedBST() {
    return verifyBalancedBST(this.root, Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  private boolean verifyBalancedBST(TreeNode<T> root, int min, int max) {
      if (root == null)
        return true;
      if (height(root.leftchild) - height(root.rightchild) >= 2) {
        log("** not a balanced tree at node " + root.id);
        return false;
      }
      if (root.id < min || root.id > max) {
        log("** not a BST tree at node " + root.id);
        return false;
      }
      return verifyBalancedBST(root.leftchild, min, root.id) &&
             verifyBalancedBST(root.rightchild, root.id, max);
  }

  private static void log(Object obj) {
    System.out.println(String.valueOf(obj));
  }

  @Test
  public void Test() {
    class Stuff {
      Stuff(int id) {}
    }
    BST<Stuff> tree = new BST<Stuff>();
    assertEquals(0, tree.size());
    assertTrue("0:[]".equals(tree.toString()));
    tree.add(20, new Stuff(20));
    assertEquals(1, tree.size());
    assertTrue("1:[[]20[]]".equals(tree.toString()));
    tree.add(18, new Stuff(18));
    tree.add(12, new Stuff(12));
    tree.add(19, new Stuff(19));
    assertEquals(4, tree.size());
    assertTrue(tree.verifyBalancedBST());
    assertTrue("4:[[[]12[]]18[[[]19[]]20[]]]".equals(tree.toString()));
    tree.add(30, new Stuff(30));
    tree.add(40, new Stuff(40));
    assertTrue("6:[[[[]12[]]18[[]19[]]]20[[]30[[]40[]]]]".equals(tree.toString()));
    assertTrue(tree.verifyBalancedBST());
    // find()
    log("BEFORE FIND TESTS " + tree.toString());
    assertNotNull(tree.find(20));
    assertNotNull(tree.find(40));
    assertNotNull(tree.find(12));
    assertNull(tree.find(10));
    assertNull(tree.find(100));
    assertNull(tree.find(21));
    // remove()
    log("BEFORE REMOVE TESTS " + tree.toString());
    tree.remove(20);
    assertTrue("5:[[[[]12[]]18[[]19[]]]30[[]40[]]]".equals(tree.toString()));
    tree.remove(200);
    assertTrue("5:[[[[]12[]]18[[]19[]]]30[[]40[]]]".equals(tree.toString()));
    tree.remove(12);
    assertTrue("4:[[[]18[[]19[]]]30[[]40[]]]".equals(tree.toString()));
    log("AFTER REMOVE TESTS " + tree.toString());
    // Check integrity of the BST
    assertTrue(tree.verifyBalancedBST());
  }
} // end class BST
