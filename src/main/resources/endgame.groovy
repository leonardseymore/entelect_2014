import za.co.entelect.challenge.agents.*

def getMap() {
  return """###################
#        #        #
# ## ### # ### ## #
# ## ### # ### ## #
#                 #
# ## # ##### # ## #
#    #   #   #    #
#### ### # ### ####
#### #       # ####
#### # ## ## # ####
A.   . #   #   B  .
#### # ## ## # ####
#### #       # ####
#### #.##### # ####
#        #        #
# ## ### # ### ## #
#  #     !.    #  #
## # # ##### # # ##
#    #   #   #    #
#*######!#.###### #
#                 #
###################
"""
}

def getA() {
  return new Optima()
}

def getB() {
    return new Dummie()
}