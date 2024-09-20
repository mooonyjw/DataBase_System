import argparse
import json
import csv
import time
class BPTreeNode:
    def __init__(self, leaf=True, b=None):
        self.leaf = leaf
        self.keys = []
        self.children = []
        self.b = b  # 명령줄에서 주어진 b 값을 사용
        self.next_leaf = None  # 리프 노드 간의 연결을 위한 포인터
        self.parent = None  # 부모 노드를 가리키는 포인터


    @property
    def max_keys(self):
        return self.b - 1  # 최대 키 개수는 b - 1개

    @property
    def min_keys(self):
        return (self.b // 2) - 1  # 최소 키 개수는 b // 2 - 1개
    
class DuplicateKeyError(Exception):
    """중복 키가 삽입될 때 발생하는 예외"""
    pass

class BPlusTree:
    def __init__(self, b):
        self.root = BPTreeNode(leaf=True, b=b)
        self.b = b

    def insert(self, key, value):
        root = self.root
        #print(f"\nInserting key {key} into tree...")

        # 리프 노드까지 찾아가서 삽입
        self.insert_non_full(root, key, value)

        # 루트가 가득 차면 루트를 분할
        if len(root.keys) > root.max_keys:
            #print(f"Root node is full after insertion, splitting root...")
            new_root = BPTreeNode(leaf=False, b=self.b)
            new_root.children.append(root)
            root.parent = new_root
            self.split_child(new_root, 0)
            self.root = new_root
        
        # 삽입 후 트리 상태 출력
        #print("\nTree after insertion:")
        #self.print_tree(self.root)

        #print_leaf_nodes(self)

    def insert_non_full(self, node, key, value):
        """리프 노드까지 내려가서 먼저 키를 삽입한 후, 분할을 처리"""
        i = len(node.keys) - 1

        if node.leaf:
            # 리프 노드에 삽입

            # 중복 키 확인
            for k, _ in node.keys:
                if k == key:
                    raise DuplicateKeyError(f"Duplicate key insertion attempted: {key}")

            node.keys.append(None)  # 공간 확보
            while i >= 0 and key < node.keys[i][0]:
                #print(f"Moving key {node.keys[i][0]} to the right")
                node.keys[i + 1] = node.keys[i]
                i -= 1
            node.keys[i + 1] = (key, value)  # 새로운 키-값 삽입

            #print(f"Inserted key {key} in leaf node: {node.keys}")

        elif not node.leaf:
            # 내부 노드에서 적절한 자식으로 이동
            while i >= 0 and key < node.keys[i]:
                #print(f"Checking key {node.keys[i]}")
                i -= 1
            i += 1
            self.insert_non_full(node.children[i], key, value)  # 재귀적으로 내려감

            # 자식 노드가 가득 찼으면 분할 수행
            if len(node.children[i].keys) > node.children[i].max_keys:
                self.split_child(node, i)
                node.children[i].parent = node
                node.children[i + 1].parent = node

    def split_child(self, node_parent, index):
        # 분할할 노드 (왼쪽 노드)
        
        left_child = node_parent.children[index]
        #print(f"Splitting child node: {left_child.keys}")
        # 새로운 노드 (오른쪽 노드)
        right_child = BPTreeNode(leaf=left_child.leaf, b=self.b)
        #print(f"New right child: {right_child.keys}")
        # 오른쪽 노드의 부모를 설정
        right_child.parent = node_parent

        if not left_child.leaf:
            # 내부 노드일 경우
            # 부모의 키와 자식 포인터를 오른쪽으로 이동하여 중간 키를 부모에 올릴 자리를 만듦
            node_parent.keys.insert(index, left_child.keys[self.b // 2])
            node_parent.children.insert(index + 1, right_child)

            # 왼쪽 노드와 오른쪽 노드에 키 분할
            right_child.keys = left_child.keys[self.b // 2 + 1:]
            left_child.keys = left_child.keys[:self.b // 2]

            # 자식 포인터들도 분할
            right_child.children = left_child.children[self.b // 2 + 1:]
            left_child.children = left_child.children[:self.b // 2 + 1]

            # 분할된 자식 노드들의 부모를 설정
            for child in right_child.children:
                child.parent = right_child
            for child in left_child.children:
                child.parent = left_child
            
            #print(f"Internal node split. Left node: {left_child.keys}, Right node: {right_child.keys}, Parent: {node_parent.keys}")

        else:
            # 리프 노드일 경우
            # 부모의 키와 자식 포인터를 오른쪽으로 이동하여 오른쪽 노드의 첫 번째 키를 부모에 올릴 자리를 만듦
            node_parent.keys.insert(index, left_child.keys[self.b // 2][0])
            node_parent.children.insert(index + 1, right_child)

            # 왼쪽 노드와 오른쪽 노드에 키 분할
            right_child.keys = left_child.keys[self.b // 2:]
            left_child.keys = left_child.keys[:self.b // 2]

            # 리프 노드 간의 연결을 유지
            right_child.next_leaf = left_child.next_leaf
            left_child.next_leaf = right_child

            #print(f"Leaf node split. Left node: {left_child.keys}, Right node: {right_child.keys}, Parent: {node_parent.keys}")


        # 분할 후 트리 상태 출력
        #print("\nTree after split:")
        #self.print_tree(self.root)

    def search(self, key):
            """주어진 키를 트리에서 검색"""
            current_node = self.root

            # 내부 노드를 지나가며 검색 경로 출력
            while not current_node.leaf:
                print(",".join(str(k) for k in current_node.keys))  # 내부 노드의 키 출력
                # 적절한 자식 노드로 이동
                i = 0
                while i < len(current_node.keys) and key >= current_node.keys[i]:
                    i += 1
                current_node = current_node.children[i]

            # 리프 노드에서 키를 검색
            for k, v in current_node.keys:
                if k == key:
                    print(v)  # 검색 성공, 값을 출력
                    return

            # 키를 찾지 못한 경우
            print("NOT FOUND")

    def range_search(self, start_key, end_key):
        """주어진 범위 내의 키-값 쌍을 검색"""
        current_node = self.root
        print(f"Searching for keys in range {start_key} to {end_key}...")
        # 내부 노드를 지나가며 적절한 리프 노드로 이동
        while not current_node.leaf:
            i = 0
            while i < len(current_node.keys) and start_key >= current_node.keys[i]:
                i += 1
            current_node = current_node.children[i]

        # 리프 노드에서 범위 내의 키-값 쌍을 찾기 시작
        while current_node is not None:
            for k, v in current_node.keys:
                if start_key <= k <= end_key:
                    print(f"{k},{v}")  # 키-값 출력
                elif k > end_key:
                    return  # 범위를 벗어나면 종료
            current_node = current_node.next_leaf  # 다음 리프 노드로 이동

    def delete(self, key):
        """트리에서 주어진 키를 삭제"""
        current_node = self.root
        node_parent = None
        parent_index = -1

        # 리프 노드로 이동하면서 삭제할 키를 찾아내기
        while not current_node.leaf:
            node_parent = current_node
            i = 0
            while i < len(current_node.keys) and key >= current_node.keys[i]:
                i += 1
            parent_index = i
            current_node = current_node.children[i]

        # Case 1: 삭제할 key가 리프 노드의 첫 번째 key가 아닌 경우
        for i, (k, v) in enumerate(current_node.keys):
            if k == key:
                if i > 0:  # 첫 번째 key가 아니면 바로 삭제
                    del current_node.keys[i]
                    print(f"Deleted key {key} from leaf node")
                    self._balance_after_deletion(current_node, key)
                    #print("\nTree after deletion:")
                    #self.print_tree(self.root)
                    self.save_to_file("index.dat")

                else:
                    # Case 2: 삭제할 key가 리프 노드의 첫 번째 key인 경우
                    print(f"Key {key} found in leaf node")
                    self._delete_key_from_index(key, current_node, node_parent, parent_index)
                    #print("After deleting key from leaf node:")
                    #self.print_tree(self.root)
                    # 내부 노드에서 key 삭제
                    self._delete_from_internal_nodes(self.root, key)
                    #print("\nTree after deletion:")
                    #self.print_tree(self.root)
                    self.save_to_file("index.dat")

                return

    

    def _delete_from_internal_nodes(self, node, key):
        """내부 노드에서 주어진 key 삭제"""
        # root부터 시작하여 key가 있는 내부 노드를 찾음
        current_node = node

        while not current_node.leaf:
            # 내부 노드에서 key가 있는지 확인
            #print("Tree after deletion:")
            #self.print_tree(self.root)
            for i in range(len(current_node.keys)):
                if current_node.keys[i] == key:
                    if current_node.leaf:
                        del current_node.keys[i]
                        return
                    # key를 찾았을 경우, 오른쪽 자식의 가장 작은 값으로 대체
                    smallest_key = self._find_smallest_key(current_node.children[i + 1])
                    print(f"Found key {key} in internal node, replacing with smallest key {smallest_key} from right child")
                    current_node.keys[i] = smallest_key
                    return

            # key의 크기를 비교해서 적절한 자식 노드로 이동
            i = 0
            while i < len(current_node.keys) and key > current_node.keys[i]:
                i += 1
            
            # 해당 key가 있을 수 있는 자식 노드로 이동
            current_node = current_node.children[i] if current_node.children else None

        #print(f"Key {key} not found in internal nodes")

    def _find_smallest_key(self, node):
        """주어진 노드에서 가장 작은 키를 찾음 (리프 노드로 내려감)"""
        current_node = node
        while not current_node.leaf:
            current_node = current_node.children[0]  # 왼쪽 자식으로 계속 내려감
        return current_node.keys[0][0]  # 리프 노드에서 가장 작은 키 반환

    def _delete_key_from_index(self, key, leaf_node, node_parent, parent_index):
        """리프 노드의 첫 번째 key 삭제 처리 및 index 업데이트"""
        # 리프 노드에서 첫 번째 key 삭제
        del leaf_node.keys[0]
        #print(f"Deleted first key {key} from leaf node")
        #print("before balance: node_parent.keys: ", node_parent.keys)
        #print("node keys: ", leaf_node.keys)
        # 리프 노드 병합 또는 재분배 처리
        self._balance_after_deletion(leaf_node, key)
        
        # index 내의 key를 inorder successor로 변경
        #print("leaf_node.keys: ", leaf_node.keys)
        #print("node parent children: ", node_parent.children[0].keys)

        #print("node parent children: ", node_parent.children[1].keys)
        if node_parent and parent_index > 0 and leaf_node.keys and leaf_node.leaf is True:

            node_parent.keys[parent_index - 1] = node_parent.children[parent_index].keys[0][0]  # 첫 번째 key로 대체
            #print(f"Updated index with key {leaf_node.keys[0][0]}")


    def _balance_after_deletion(self, node, key):
        """삭제 후 균형을 맞추기 위한 병합 및 재분배 수행"""
        min_keys = (self.b - 1) // 2
        #print(f"Balancing node: {node.keys}")
        # 노드에 충분한 키가 남아 있으면 아무 것도 하지 않음
        if len(node.keys) >= min_keys and node != self.root:
            return
        
        # 부모 노드 가져오기
        node_parent = node.parent
        #print("node_parent: ", node_parent.keys)
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

        # 왼쪽 형제 노드 가져오기
        left_sibling = None if parent_index == 0 else node_parent.children[parent_index - 1]
        '''if left_sibling:
            print(f"Left sibling: {left_sibling.keys}")
        else: print("No left sibling")'''
        right_sibling = None if parent_index == len(node_parent.children) - 1 else node_parent.children[parent_index + 1]
        '''if right_sibling:
            print(f"Right sibling: {right_sibling.keys}")
        else: print("No right sibling")'''

        # 왼쪽 형제에서 재분배 또는 병합 (왼쪽 형제한테 지원을 받음)
        if left_sibling and len(left_sibling.keys) > min_keys:
            if not node.leaf:
                #print("parent_index", parent_index)
                #print("node parent keys: ", node_parent.keys[parent_index-1])
                node.keys.insert(0, node_parent.keys[parent_index-1])

                #print("node keys: ", node.keys)
                child_node = left_sibling.children.pop()
                node.children.insert(0, child_node)

                child_node.parent = node

                #print("left_sibling.keys.pop(): ",left_sibling.keys.pop())
                node_parent.keys[parent_index - 1] = left_sibling.keys.pop()
            if node.leaf:
                node.keys.insert(0, left_sibling.keys.pop())
                node_parent.keys[parent_index - 1] = node.keys[0][0]
            #print(f"Rebalanced with left sibling")
        
        # 오른쪽 형제에서 재분배 또는 병합 (오른쪽 형제한테 지원을 받음)
        elif right_sibling and len(right_sibling.keys) > min_keys:
            if not node.leaf:
                node.keys.append(node_parent.keys[parent_index])
                #print("right_sibling.children.pop(0): ", right_sibling.children.pop(0).keys)
                # 오른쪽 형제에서 자식을 가져와서 현재 노드에 추가하고, 부모 설정을 업데이트
                child_node = right_sibling.children.pop(0)
                node.children.append(child_node)
                
                child_node.parent = node
                
                #print("node.keys: ", node.keys)
                #print("node children0: ", node.children[0].keys)
                #print("node children1: ", node.children[1].keys)
                #print("print tree")
                #self.print_tree(self.root)
                node_parent.keys[parent_index] = right_sibling.keys.pop(0)
            if node.leaf: #?? 여기도 이상함. node에다가 직접 붙이는게 아니라 부모꺼 붙여야함.
                node.keys.append(right_sibling.keys.pop(0))
                node_parent.keys[parent_index] = right_sibling.keys[0][0]
            #print(f"Rebalanced with right sibling")

        # 병합 수행 (병합하고 부모한테 지원을 받음)
        else:
            # 왼쪽 형제와 병합
            if left_sibling and len(left_sibling.keys) + len(node.keys) <= self.b - 1:
                # 왼쪽 형제와 병합
                if node.leaf:
                    left_sibling.keys.extend(node.keys)
                    left_sibling.next_leaf = node.next_leaf  # 리프 노드의 next_leaf 연결
                else:
                    left_sibling.keys.append(node_parent.keys[parent_index - 1])                
                    left_sibling.keys.extend(node.keys)
                    left_sibling.children.extend(node.children)
                    for child in node.children:
                        child.parent = left_sibling

                            
                # 부모 노드에서 현재 노드 삭제
                del node_parent.children[parent_index]
                '''if node_parent == self.root and len(self.root.keys)==1 and key != node_parent.keys[parent_index-1]:
                    self._balance_after_deletion(node_parent, key)

                else:'''
                #if key == node_parent.keys[parent_index-1]:
                del node_parent.keys[parent_index - 1]


                #print(f"Merged with left sibling: {left_sibling.keys}")
            
            elif right_sibling and len(right_sibling.keys) + len(node.keys) <= self.b - 1:                # 오른쪽 형제와 병합
                if node.leaf:
                    node.keys.extend(right_sibling.keys)
                    node.next_leaf = right_sibling.next_leaf  # 리프 노드의 next_leaf 연결
                else:
                    # 현재 노드와 오른쪽 형제를 병합
                    node.keys.append(node_parent.keys[parent_index])  # 부모의 키를 병합
                    node.keys.extend(right_sibling.keys)
                    node.children.extend(right_sibling.children)
                    for child in right_sibling.children:
                        child.parent = node  # 부모 재설정

                # 부모 노드에서 오른쪽 형제 삭제
                del node_parent.children[parent_index + 1]

                # 루트 노드 처리
                '''if node_parent == self.root and len(self.root.keys) == 1 and key != node_parent.keys[parent_index]:
                    self._balance_after_deletion(node_parent, key)
                else:'''
                    # 부모 노드에서 병합된 키 삭제
                del node_parent.keys[parent_index]


        
                # 부모 노드 처리: 재분배 또는 병합
            '''if len(node_parent.keys) == 0 and node_parent != self.root:
                grand_parent = node_parent.parent
                if grand_parent:
                    parent_index = grand_parent.children.index(node_parent)
                    # 부모 노드를 삭제하고 자식을 위로 올림
                    grand_parent.children[parent_index] = node_parent.children[0]
                    node_parent.children[0].parent = grand_parent
                    #print(f"Removed internal node with single child. Updated grandparent: {grand_parent.keys}")
                else:
                    # 루트가 내부 노드였고, 자식이 하나만 남은 경우
                    self.root = node_parent.children[0]
                    self.root.parent = None
                    #print(f"Root node replaced by its single child: {self.root.keys}")
'''
            #print("Tree after balancing:")
            #self.print_tree(self.root)
            # 부모 노드가 최소 키 개수 미만이면 부모도 재분배 또는 병합해야 함
            if node_parent and len(node_parent.keys) < min_keys:
                self._balance_after_deletion(node_parent, key)


    def delete_from_file(self, data_file):
        """CSV 파일에서 삭제할 키들을 읽어와 트리에서 삭제"""
        with open(data_file, 'r') as f:
            for line in f:
                key = int(line.strip())
                self.delete(key)
    
    def print_tree(self, node, level=0):
        indent = "    " * level
        if node.leaf:
            print(f"{indent}Leaf Node: {node.keys}")
        else:
            print(f"{indent}Internal Node: {node.keys}")
            for child in node.children:
                self.print_tree(child, level + 1)

    def save_to_file(self, filename):
        """B+ 트리 구조를 원하는 형식으로 파일에 저장"""
        with open(filename, 'w') as f:
            # 차수 b 값을 먼저 기록
            f.write(f'b: {self.b}\n')

            # 트리의 루트를 포함한 구조를 기록
            self._write_node_to_file(f, self.root)

    def load_from_file(self, filename):
        """파일에서 B+ 트리 구조를 로드"""
        with open(filename, 'r') as f:
            # 첫 줄에서 차수 b 값을 읽음
            self.b = int(f.readline().split(': ')[1])

            # 나머지 파일에서 노드 정보를 읽어들임
            self.root, leaf_nodes = self._read_node_from_file(f)

            # 리프 노드 간의 next_leaf 연결 복구
            self._restore_leaf_connections(leaf_nodes)

    def _read_node_from_file(self, f, level=0, parent = None):
        """파일에서 노드를 읽어들여 재귀적으로 트리 구조를 복원"""
        indent = '    ' * level
        line = f.readline().strip()

        # 디버그: 읽어들인 줄 출력
        #print(f"Reading line at level {level}: '{line}'")
        leaf_nodes = []
        if line.startswith(f"Leaf Node:"):
            # 리프 노드의 경우, 키와 값을 복원
            node = BPTreeNode(leaf=True, b=self.b)
            node.parent = parent
            keys_str = line.split("Leaf Node: ")[1].strip("[]")  # 리스트 형태로 저장된 키와 값들
            #print(f"Keys string: {keys_str}")  # 디버그 출력
            
            if keys_str:
            # 작은 따옴표와 괄호를 제거한 후 파싱
                pairs = keys_str.split('), (')  # 각 키-값 쌍을 분리
                #print(f"Pairs: {pairs}")  # 디버그 출력
                for pair in pairs:
                    # (9, '87632') -> 9, 87632 형식으로 변환
                    pair = pair.replace('(', '').replace(')', '').replace("'", "").strip()
                    key, value = pair.split(', ')
                    node.keys.append((int(key), value.strip()))  # 키는 int, 값은 string

            #print(f"Restored Leaf Node: {node.keys}")  # 디버그 출력

            leaf_nodes.append(node)  # 리프 노드 저장
            #print(f"{indent}Leaf Node: {node.keys}")  # 디버그 출력
            return node, leaf_nodes
        elif line.startswith(f"Internal Node:"):
            # 내부 노드의 경우, 키와 자식 노드를 복원
            node = BPTreeNode(leaf=False, b=self.b)
            node.parent = parent
            keys_str = line.split("Internal Node: ")[1].strip("[]")
            if keys_str:
                node.keys = [int(k) for k in keys_str.split(',')]  # 내부 노드는 키만 저장
            #print(f"Restored Internal Node: {node.keys}")  # 디버그 출력

            # 자식 노드를 재귀적으로 읽어들임
            for _ in range(len(node.keys) + 1):
                child_node, child_leaf_nodes = self._read_node_from_file(f, level + 1, parent=node)
                if child_node is None:
                    print(f"Error: Failed to read child node at level {level + 1}")
                    break
                node.children.append(child_node)
                leaf_nodes.extend(child_leaf_nodes)  # 리프 노드들을 모아둠

            return node, leaf_nodes
        else:
            print(f"Error: Unexpected line format at level {level}: '{line}'")
    
        return None, leaf_nodes  # 트리가 비어있으면 None 반환

    def _restore_leaf_connections(self, leaf_nodes):
        """리프 노드 간의 next_leaf 연결 복구"""
        for i in range(len(leaf_nodes) - 1):
            leaf_nodes[i].next_leaf = leaf_nodes[i + 1]


    def _write_node_to_file(self, f, node, level=0):
        """재귀적으로 노드 정보를 파일에 기록"""
        indent = '    ' * level  # 들여쓰기
        if node.leaf:
            # 리프 노드인 경우, 키와 값을 기록
            f.write(f"{indent}Leaf Node: {node.keys}\n")
        else:
            # 내부 노드인 경우, 키와 자식 노드를 기록
            f.write(f"{indent}Internal Node: {node.keys}\n")
            for child in node.children:
                self._write_node_to_file(f, child, level + 1)

def create_index_file(index_file, node_size):
    # node_size를 b로 설정하여 새로운 B+ 트리를 초기화합니다.
    tree = BPlusTree(b=node_size)
    
    # 빈 트리를 index_file에 저장합니다.
    tree.save_to_file(index_file)
    print(f"Created index file '{index_file}' with node size {node_size}.")

def insert_into_index(index_file, data_file):
    try:
        # index_file에서 기존 B+ 트리를 로드합니다.
        tree = BPlusTree(0)  # 일단 b 값은 load_from_file에서 덮어씌워짐
        tree.load_from_file(index_file)
    except FileNotFoundError:
        print(f"Index file '{index_file}' not found.")
        return

    # CSV 파일에서 키-값 쌍을 삽입합니다.
    with open(data_file, mode='r') as file:
        csv_reader = csv.reader(file)
        for row in csv_reader:
            if len(row) == 2:
                key = int(row[0].strip())
                value = row[1].strip()
                tree.insert(key, value)
    
    # 수정된 트리를 index_file에 저장합니다.
    tree.save_to_file(index_file)
    print(f"Inserted data from '{data_file}' into '{index_file}'.")

def print_index_file(index_file):
    try:
        # index_file에서 기존 B+ 트리를 로드합니다.
        tree = BPlusTree(0)  # 일단 b 값은 load_from_file에서 덮어씌워짐
        tree.load_from_file(index_file)
    except FileNotFoundError:
        print(f"Index file '{index_file}' not found.")
        return

    # B+ 트리 구조를 출력합니다.
    def print_tree(node, level=0, direction="root"):
        if not node:  # 노드가 비어 있으면 (empty tree!)
            print("Empty tree!!")
            return

        # 현재 레벨과 방향 출력
        print(f"Level {level} [{direction}] :", end="   ")

        # 각 레벨의 들여쓰기를 위한 공백 추가
        for _ in range(level - 1):
            print("            ", end="")

        # 현재 노드의 키 출력
        for key, value in node.keys:
            print(f"{key}", end=" ")

        print()  # 다음 줄로 이동

        level += 1  # 다음 레벨로 증가

        # 자식 노드를 재귀적으로 출력
        if node.children:
            for i, child in enumerate(node.children):
                if i == 0:
                    print_tree(child, level, "left")
                elif i == len(node.children) - 1:
                    print_tree(child, level, "right")
                else:
                    print_tree(child, level, "middle")

    print_tree(tree.root)

def print_leaf_nodes(self):
    # 리프 노드들을 순차적으로 탐색하여 출력
    current = self.root
    # 리프 노드까지 내려감
    while not current.leaf:
        current = current.children[0]
    
    # 리프 노드 간 연결 확인
    print("\nLeaf nodes:")
    while current is not None:
        print(f"Leaf: {current.keys}")
        current = current.next_leaf


def main():
    parser = argparse.ArgumentParser(description="B+ Tree Program")
    
    # Argument for index file creation
    parser.add_argument('-c', '--create', nargs=2, help="Create index file", metavar=('index_file', 'b'))
    
    # Argument for inserting key-value pairs from a CSV file
    parser.add_argument('-i', '--insert', nargs=2, help="Insert into index file", metavar=('index_file', 'data_file'))

    # Argument for printing the B+ tree
    parser.add_argument('-p', '--print', help="Print B+ Tree structure from index file", metavar='index_file')

    parser.add_argument('-s', '--search', help="Search for a key in the B+ Tree", nargs=2, metavar=('index_file', 'key'))

    parser.add_argument('-r', '--range_search', help='Search for keys in the given range in the index file', nargs=3, metavar=('index_file', 'start_key', 'end_key'))

    parser.add_argument('-d', '--delete', help='Delete keys from the index file using a CSV file', nargs=2, metavar=('index_file', 'data_file'))

    args = parser.parse_args()

    if args.create:
        index_file, node_size = args.create
        node_size = int(node_size)  # 노드 크기를 가져와서 정수로 변환
        create_index_file(index_file, node_size)

    if args.insert:
        print(f"Inserting data...")
        start=time.time()
        index_file, data_file = args.insert
        insert_into_index(index_file, data_file)
        end = time.time()
        print("Time: ", end-start)

    if args.search:
        
        index_file, key = args.search
        key = int(key)  # 키를 정수로 변환

        # index_file에서 트리 로드
        tree = BPlusTree(0)  # 일단 b 값은 load_from_file에서 덮어씌워짐
        print("Loading tree from file...")
        tree.load_from_file(index_file)  # 예시 로드 함수

        # 키 검색 실행
        tree.search(key)

    if args.range_search:
        index_file, start_key, end_key = args.range_search
        start_key, end_key = int(start_key), int(end_key)  # 키를 정수로 변환

        # index_file에서 트리 로드
        tree = BPlusTree(0)  # 일단 b 값은 load_from_file에서 덮어씌워짐
        tree.load_from_file(index_file)

        # 범위 검색 실행
        tree.range_search(start_key, end_key)


    # Deletion
    if args.delete:
        index_file, data_file = args.delete

        # index_file에서 트리 로드
        tree = BPlusTree(0)  # 일단 b 값은 load_from_file에서 덮어씌워짐
        tree.load_from_file(index_file)

        # CSV 파일에서 키 삭제
        tree.delete_from_file(data_file)

    if args.print:
        index_file = args.print
        print_index_file(index_file)

if __name__ == "__main__":
    main()
