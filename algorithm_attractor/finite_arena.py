class finite_arena:
	preds = []
	players = []

	def __init__(self, n):
		self.preds = [[] for i in range(n)]
		self.players = [0 for i in range(n)]

	def addPreds(self, index, pred_list):
		self.preds[index].extend(pred_list)

	def setPlayers(self, players):
		if len(players) == len(self.preds):
			self.players = players

	def attractor(self, F, player=0, i=-1):

		### Pre-processing
		# for each vertice in the graph, compute its outdegree
		# this is done by checking the predecessors list and incrementing a node's counter everytime we encounter it
		out_degrees = [0 for j in range(len(self.preds))]
		for node in self.preds:
			for pred in node:
				out_degrees[pred]+=1

		### initializing lists
		# attractor has one entry per node. Value 1 at a given index means the node corresponding to that index is in the attractor. 0 otherwise.
		attractor = [0]*len(self.preds)
		for index in F:
			attractor[index]=1

		# attractor_new are the nodes added to the attractor at the current iteration.
		attractor_new = F

		### Computing
		# stops when attr_i = attr_i+1 OR when we reached the desired attractor number. 
		while len(attractor_new) > 0 and i!=0:

			# empty attractor_new and remember which node to check.
			to_check = attractor_new
			attractor_new = []
			for index in to_check:

				# for all predecessors of the nodes to check
				for pred in self.preds[index]:

					# if predecessor is not already in the attractor
					if attractor[pred]==0:

						# if predecessor belongs to j0
						if self.players[pred]==player:

							# mark predecessor
							attractor_new.append(pred)

						# if predecessor belongs to j1
						else:

							# decrement it's outdegree counter
							out_degrees[pred]-=1

							# if the counter reached 0, mark the predecessor
							if out_degrees[pred] == 0:
								attractor_new.append(pred)

			# add newly marked nodes in the attractor
			for index in attractor_new:
				attractor[index] = 1
				
			i-=1

		return attractor

if __name__ == "__main__":
	arena = finite_arena(10)
	arena.addPreds(0, [6,3,2])
	arena.addPreds(1, [2])
	arena.addPreds(2, [3])
	arena.addPreds(3, [0,1,4])
	arena.addPreds(4, [5,6,9])
	arena.addPreds(5, [7])
	arena.addPreds(6, [5,8])
	arena.addPreds(7, [5,8,9])
	arena.addPreds(8, [9,7])
	arena.addPreds(9, [6,8])

	arena.setPlayers([0,0,1,1,1,1,0,0,1,0])

	print(arena.attractor([0,1], 0))
