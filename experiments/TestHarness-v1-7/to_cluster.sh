#!/usr/bin/env python3

import sys

W=19
H=22

board = []
visited = [0 for i in range(W * H)]

for line in sys.stdin:
	board.append(list(line.rstrip()))
print(board)
print(board[1][2])
