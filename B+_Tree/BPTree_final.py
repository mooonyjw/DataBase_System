import argparse
import csv

class BPTreeNode:
    def __init__(self, leaf=True, b=None):
        self.leaf = leaf
        self.keys = []
        self.children = []
        self.b = b 
        self.next_leaf = None  # Linked list of leaf nodes
        self.parent = None  # Pointer to the parent node
    
class DuplicateKeyError(Exception):
    # Duplicate key error
    pass

class BPlusTree:
    def __init__(self, b):
        self.root = BPTreeNode(leaf=True, b=b)
        self.b = b

    def insert(self, key, value):
        root = self.root

        # Insert key-value pair into the leaf node
        self.insert_in_leaf_node(root, key, value)

        # If root is full, split the root
        if len(root.keys) > self.b - 1:
            #print("Root node is full after insertion, split root")
            new_root = BPTreeNode(leaf=False, b=self.b)
            new_root.children.append(root)
            root.parent = new_root
            self.split_child(new_root, 0)
            self.root = new_root
        
    def insert_in_leaf_node(self, node, key, value):
        # Find leaf node to insert
        i = len(node.keys) - 1

        if node.leaf:

            # Check for duplicate key
            for k, _ in node.keys:
                if k == key:
                    raise DuplicateKeyError(f"Duplicate key insertion attempted: {key}")

            node.keys.append(None) 
            while i >= 0 and key < node.keys[i][0]:
                #print(f"Moving key {node.keys[i][0]} to the right")
                node.keys[i + 1] = node.keys[i]
                i -= 1
            node.keys[i + 1] = (key, value)  # insert new key-value pair

            #print(f"Inserted key {key} in leaf node: {node.keys}")

        elif not node.leaf:
            # Find leaf node to insert
            while i >= 0 and key < node.keys[i]:
                #print(f"Checking key {node.keys[i]}")
                i -= 1
            i += 1
            self.insert_in_leaf_node(node.children[i], key, value)

            # If leaf node is full after insertion, split the node
            if len(node.children[i].keys) > node.children[i].max_keys:
                self.split_child(node, i)
                node.children[i].parent = node
                node.children[i + 1].parent = node

    def split_child(self, node_parent, index):

        # Node to be split (left node)
        left_child = node_parent.children[index]

        # New node (right node)
        right_child = BPTreeNode(leaf=left_child.leaf, b=self.b)
        right_child.parent = node_parent

        if not left_child.leaf:
            # Internal node split
            # Make room for the new key in the parent node
            node_parent.keys.insert(index, left_child.keys[self.b // 2])
            node_parent.children.insert(index + 1, right_child)

            # Split keys between the left and right children
            right_child.keys = left_child.keys[self.b // 2 + 1:]
            left_child.keys = left_child.keys[:self.b // 2]

            # Split child pointers between the left and right children
            right_child.children = left_child.children[self.b // 2 + 1:]
            left_child.children = left_child.children[:self.b // 2 + 1]

            # Set parent pointers for child nodes
            for child in right_child.children:
                child.parent = right_child
            for child in left_child.children:
                child.parent = left_child
            
            #print(f"Internal node split. Left node: {left_child.keys}, Right node: {right_child.keys}, Parent: {node_parent.keys}")

        else:
            # Leaf node split
            # Make room for the new key in the parent node
            node_parent.keys.insert(index, left_child.keys[self.b // 2][0])
            node_parent.children.insert(index + 1, right_child)

            # Split keys between the left and right children
            right_child.keys = left_child.keys[self.b // 2:]
            left_child.keys = left_child.keys[:self.b // 2]

            # Update next_leaf pointers
            right_child.next_leaf = left_child.next_leaf
            left_child.next_leaf = right_child

            #print(f"Leaf node split. Left node: {left_child.keys}, Right node: {right_child.keys}, Parent: {node_parent.keys}")

    def search(self, key):
        # Search for the key in the B+ tree
        
        current_node = self.root

        # Print the traversal path
        while not current_node.leaf:
            print(",".join(str(k) for k in current_node.keys))
            
            # Move until a leaf node is reached
            i = 0
            while i < len(current_node.keys) and key >= current_node.keys[i]:
                i += 1
            current_node = current_node.children[i]

        # Search for the key in the leaf node and print the value
        for k, v in current_node.keys:
            if k == key:
                print(v)
                return

        # Failed to find the key
            print("NOT FOUND")

    def range_search(self, start_key, end_key):
        # Search for keys in the given range

        current_node = self.root

        # Traverse to the leaf node
        while not current_node.leaf:
            i = 0
            while i < len(current_node.keys) and start_key >= current_node.keys[i]:
                i += 1
            current_node = current_node.children[i]

        # Search for keys in the range and print the key-value pairs
        while current_node is not None:
            for k, v in current_node.keys:
                if start_key <= k <= end_key:
                    print(f"{k},{v}")
                elif k > end_key:
                    return
            current_node = current_node.next_leaf

    def delete(self, key):
        # Delete the key from the B+ tree

        current_node = self.root
        node_parent = None
        parent_index = -1

        # Search for the leaf node containing the key
        while not current_node.leaf:
            node_parent = current_node
            i = 0
            while i < len(current_node.keys) and key >= current_node.keys[i]:
                i += 1
            parent_index = i
            current_node = current_node.children[i]

        for i, (k, v) in enumerate(current_node.keys):
            if k == key:
                if i > 0:
                    # Case 1: Non-first key in leaf node
                    
                    del current_node.keys[i]
                    #print("Deleted key {key} from leaf node")
                    self.balance_after_deletion(current_node, key)
                    self.save_to_file("index.dat")

                else:
                    # Case 2: First key in leaf node
                    
                    # Delete the key from the leaf node
                    self.delete_key_from_leaf_node(key, current_node, node_parent, parent_index)
                    
                    # Delete the key from the internal nodes
                    self.delete_key_from_internal_node(self.root, key)
                    self.save_to_file("index.dat")

                return
    
    def delete_key_from_internal_node(self, node, key):
        # Delete the key from the internal nodes

        current_node = node
        while not current_node.leaf:

            # Search for the key in the internal node
            for i in range(len(current_node.keys)):    
                if current_node.keys[i] == key:
                    # Replace with inorder successor
                    smallest_key = self.find_smallest_key(current_node.children[i + 1])
                    current_node.keys[i] = smallest_key
                    return

            # Search for the child node to traverse
            i = 0
            while i < len(current_node.keys) and key > current_node.keys[i]:
                i += 1
            current_node = current_node.children[i] if current_node.children else None

    def find_smallest_key(self, node):
        # Find the smallest key in the subtree

        current_node = node
        while not current_node.leaf:
            current_node = current_node.children[0]
        return current_node.keys[0][0]

    def delete_key_from_leaf_node(self, key, leaf_node, node_parent, parent_index):
        # Delete the key from the leaf node

        del leaf_node.keys[0]

        # Balance the tree after deletion
        self.balance_after_deletion(leaf_node, key)
        
        # Update the parent key if the first key in the leaf node is deleted
        if node_parent and parent_index > 0 and leaf_node.keys and leaf_node.leaf is True:
            node_parent.keys[parent_index - 1] = node_parent.children[parent_index].keys[0][0]


    def balance_after_deletion(self, node, key):
        # Balance the B+ tree after deletion

        min_keys = (self.b - 1) // 2

        # Check if the node has enough keys
        if len(node.keys) >= min_keys and node != self.root:
            return
        
        node_parent = node.parent

        if not node_parent:
        # 루트 노드 처리: 루트가 내부 노드인데 자식이 하나만 남으면 자식을 새로운 루트로 설정
            if node == self.root and len(node.children) == 1:
                #print("Root node has single child. Replacing root with child...")
                if not node.children:
                    del node.keys[0]
                    return
                single_child = node.children[0]
                # 루트 노드의 키를 자식에게 넘김
                single_child.keys = node.keys + single_child.keys  # 루트의 키와 자식의 키 병합

                # 자식을 새로운 루트로 설정
                self.root = single_child
                self.root.parent = None  # 부모 관계 제거
            return
        parent_index = node_parent.children.index(node)

        # Check if the left and right siblings exist
        left_sibling = None if parent_index == 0 else node_parent.children[parent_index - 1]
        right_sibling = None if parent_index == len(node_parent.children) - 1 else node_parent.children[parent_index + 1]

        # Rebalancing with left sibling
        if left_sibling and len(left_sibling.keys) > min_keys:

            # If node is a leaf node, just get key from left sibling and update parent key
            if node.leaf:
                node.keys.insert(0, left_sibling.keys.pop())
                node_parent.keys[parent_index - 1] = node.keys[0][0]

            # If node is an internal node get key from parent and left sibling should give its last key to parent
            else:
                node.keys.insert(0, node_parent.keys[parent_index-1])
                child_node = left_sibling.children.pop()
                node.children.insert(0, child_node)
                child_node.parent = node
                node_parent.keys[parent_index - 1] = left_sibling.keys.pop()

            
        
        # Rebalancing with right sibling
        elif right_sibling and len(right_sibling.keys) > min_keys:
            
            # If node is a leaf node, just get key from right sibling and update parent key
            if node.leaf:
                node.keys.append(right_sibling.keys.pop(0))
                node_parent.keys[parent_index] = right_sibling.keys[0][0]
            
            # If node is an internal node get key from parent and right sibling should give its first key to parent
            else:
                node.keys.append(node_parent.keys[parent_index])
                child_node = right_sibling.children.pop(0)
                node.children.append(child_node)
                child_node.parent = node
                node_parent.keys[parent_index] = right_sibling.keys.pop(0)

        # Merging nodes and parent key
        else:
            # Merge with left sibling
            if left_sibling and len(left_sibling.keys) + len(node.keys) <= self.b - 1:

                # If node is a leaf node, merge with left sibling and update leaf pointers
                if node.leaf:
                    left_sibling.keys.extend(node.keys)
                    left_sibling.next_leaf = node.next_leaf

                # If node is an internal node, get parent key and merge with left sibling
                else:
                    left_sibling.keys.append(node_parent.keys[parent_index - 1])                
                    left_sibling.keys.extend(node.keys)
                    left_sibling.children.extend(node.children)
                    # Update parent pointers for child nodes
                    for child in node.children:
                        child.parent = left_sibling

                # Delete child node
                del node_parent.children[parent_index]
                
                # Delete parent key
                del node_parent.keys[parent_index - 1]
            
            # Merge with right sibling
            elif right_sibling and len(right_sibling.keys) + len(node.keys) <= self.b - 1:

                # If node is a leaf node, merge with right sibling and update leaf pointers
                if node.leaf:
                    node.keys.extend(right_sibling.keys)
                    node.next_leaf = right_sibling.next_leaf

                # If node is an internal node, get parent key and merge with right sibling
                else:
                    node.keys.append(node_parent.keys[parent_index])
                    node.keys.extend(right_sibling.keys)
                    node.children.extend(right_sibling.children)
                    # Update parent pointers for child nodes
                    for child in right_sibling.children:
                        child.parent = node

                # Delete child node
                del node_parent.children[parent_index + 1]

                # Delete parent key
                del node_parent.keys[parent_index]

            # Balance the parent node after merging if it has less keys than min_keys
            if node_parent and len(node_parent.keys) < min_keys:
                self.balance_after_deletion(node_parent, key)

    def delete_from_file(self, data_file):
        # Read file and delete keys

        with open(data_file, 'r') as f:
            for line in f:
                key = int(line.strip())
                self.delete(key)
    
    def print_tree(self, node, level=0):
        # Print the B+ tree structure

        indent = '    ' * level
        if node.leaf:
            print(f"{indent}Leaf Node: {node.keys}")
        else:
            print(f"{indent}Internal Node: {node.keys}")
            for child in node.children:
                self.print_tree(child, level + 1)

    def save_to_file(self, filename):
        # Save B+ tree structure to a file

        with open(filename, 'w') as f:
            f.write(f'b: {self.b}\n')

            self.write_tree_to_file(f, self.root)

    def write_tree_to_file(self, f, node, level=0):
        # Write B+ tree structure to a file

        indent = '    ' * level
        if node.leaf:
            f.write(f"{indent}Leaf Node: {node.keys}\n")
        else:
            f.write(f"{indent}Internal Node: {node.keys}\n")
            for child in node.children:
                self.write_tree_to_file(f, child, level + 1)

    def load_from_file(self, filename):
        # Load B+ tree structure from a file

        with open(filename, 'r') as f:
            self.b = int(f.readline().split(': ')[1])
            self.root, leaf_nodes = self._read_node_from_file(f)
            self._restore_leaf_connections(leaf_nodes)

    def read_node_from_file(self, f, level=0, parent = None):
        # Read B+ tree structure from a file and restore the tree

        indent = '    ' * level
        line = f.readline().strip()

        leaf_nodes = []
        if line.startswith(f"Leaf Node:"):
            # If leaf node, restore keys and values
            node = BPTreeNode(leaf=True, b=self.b)
            node.parent = parent
            keys_str = line.split("Leaf Node: ")[1].strip("[]")
            
            if keys_str:
                # Split key-value pairs
                pairs = keys_str.split('), (')
                for pair in pairs:
                    pair = pair.replace('(', '').replace(')', '').replace("'", "").strip()
                    key, value = pair.split(', ')
                    node.keys.append((int(key), value.strip()))

            # Store leaf node
            leaf_nodes.append(node)
            return node, leaf_nodes
        
        elif line.startswith(f"Internal Node:"):
            # If internal node, restore only keys
            node = BPTreeNode(leaf=False, b=self.b)
            node.parent = parent
            keys_str = line.split("Internal Node: ")[1].strip("[]")

            if keys_str:
                node.keys = [int(k) for k in keys_str.split(',')] 

            # Recursively read child nodes
            for _ in range(len(node.keys) + 1):
                child_node, child_leaf_nodes = self.read_node_from_file(f, level + 1, parent=node)
                if child_node is None:
                    print(f"Error: Failed to read child node at level {level + 1}")
                    break
                node.children.append(child_node)
                leaf_nodes.extend(child_leaf_nodes)  # Add leaf nodes to the list

            return node, leaf_nodes
        
        else:
            print(f"Error: Unexpected line format at level {level}: '{line}'")
    
        return None, leaf_nodes  # Return None if node is not found

    def restore_leaf_connections(self, leaf_nodes):
        # Restore connections between leaf nodes

        for i in range(len(leaf_nodes) - 1):
            leaf_nodes[i].next_leaf = leaf_nodes[i + 1]

def create_index_file(index_file, node_size):
    
    # Create an empty B+ tree
    tree = BPlusTree(b=node_size)
    
    # Save the B+ tree to a index_file
    tree.save_to_file(index_file)
    print(f"Created index file '{index_file}' with node size {node_size}.")

def insert_into_index(index_file, data_file):
    # Insert key-value pairs into the B+ tree and save the updated tree to the index file

    try:
        # Load the existing B+ tree from the index file
        tree = BPlusTree(0)  # b will be overwritten by load_from_file
        tree.load_from_file(index_file)

    except FileNotFoundError:
        print(f"Index file '{index_file}' not found.")
        return

    # Read data from the CSV file and insert into the B+ tree
    with open(data_file, mode='r') as file:
        csv_reader = csv.reader(file)
        for row in csv_reader:
            if len(row) == 2:
                key = int(row[0].strip())
                value = row[1].strip()
                tree.insert(key, value)
    
    # Save the updated B+ tree to the index file
    tree.save_to_file(index_file)
    print(f"Inserted data from '{data_file}' into '{index_file}'.")

def print_index_file(index_file):
    # Print the B+ tree structure from the index file

    try:
        # Load the existing B+ tree from the index file
        tree = BPlusTree(0)  # b will be overwritten by load_from_file
        tree.load_from_file(index_file)
        
    except FileNotFoundError:
        print(f"Index file '{index_file}' not found.")
        return


def main():
    parser = argparse.ArgumentParser(description="B+ Tree Program")
    
    # Argument for index file creation
    parser.add_argument('-c', '--create', nargs=2, help="Create index file", metavar=('index_file', 'b'))
    
    # Argument for inserting key-value pairs into the B+ tree and saving the updated tree to the index file
    parser.add_argument('-i', '--insert', nargs=2, help="Insert into index file", metavar=('index_file', 'data_file'))

    # Argument for printing the B+ tree
    parser.add_argument('-p', '--print', help="Print B+ Tree structure from index file", metavar='index_file')

    # Argument for searching for a key in the B+ tree
    parser.add_argument('-s', '--search', help="Search for a key in the B+ Tree", nargs=2, metavar=('index_file', 'key'))

    # Argument for searching for keys in a given range in the B+ tree
    parser.add_argument('-r', '--range_search', help='Search for keys in the given range in the index file', nargs=3, metavar=('index_file', 'start_key', 'end_key'))

    # Argument for deleting keys in the B+ tree and update the index file
    parser.add_argument('-d', '--delete', help='Delete keys from the index file using a CSV file', nargs=2, metavar=('index_file', 'data_file'))

    args = parser.parse_args()

    if args.create:
        print("Creating index file")
        index_file, node_size = args.create
        node_size = int(node_size)
        create_index_file(index_file, node_size)

    if args.insert:
        print(f"Inserting data")
        index_file, data_file = args.insert
        insert_into_index(index_file, data_file)

    if args.search:
        print("Searching for key")
        index_file, key = args.search
        key = int(key)

        # Load tree from index file
        tree = BPlusTree(0)
        print("Loading tree from file")
        tree.load_from_file(index_file)

        tree.search(key)

    if args.range_search:
        print("Searching for keys in range")
        index_file, start_key, end_key = args.range_search
        start_key, end_key = int(start_key), int(end_key)

        # Load tree from index file
        tree = BPlusTree(0)
        tree.load_from_file(index_file)

        tree.range_search(start_key, end_key)

    if args.delete:
        print("Deleting keys")
        index_file, data_file = args.delete

        # Load tree from index file
        tree = BPlusTree(0)
        tree.load_from_file(index_file)

        tree.delete_from_file(data_file)

    if args.print:
        print("Printing B+ Tree structure")
        index_file = args.print
        print_index_file(index_file)

if __name__ == "__main__":
    main()