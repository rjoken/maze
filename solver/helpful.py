import sys

class RequiredOptions:
	# items in this list must be resolved before program continuation
	def __init__(self, options=[]):
		self.required_options = options 
		
	def add(self, option):
		if option not in self.required_options:
			self.required_options.append(option)
			
	def resolve(self, option):
		if option in self.required_options:
			self.required_options.remove(option)
			
	def optionsResolved(self):
		if len(self.required_options):
			return False 
		else:
			return True
			
def bound(n, minimum, maximum):
	# returns n bound between two values minimum and maximum
	# < minimum returns minimum, > maximum returns maximum
	return int(max(minimum, min(maximum, n)))
	
def hamming_weight(n):
	# counts the number of ones in the binary representation of n
	ret = 0
	while n != 0:
		ret += n & 1
		n >>= 1
	return ret
	
def usage_and_exit():
	# prints the usage of this program, then exits
	print("Usage: solve.py -i <inputfile>\nOptional: -h (help), -m (don't print maze structure), -o <filename> (output statistics to text file)")
	sys.exit()