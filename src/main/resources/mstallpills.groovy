import za.co.entelect.challenge.agents.Greedy
import za.co.entelect.challenge.agents.Layzie

def getMap() {
  return """###################
#        #.      .#
# ## ### # ### ##.#
# ## ### # ### ## #
#                 #
#.## # ##### # ##.#
#    #   #   #   .#
#### ### # ### ####
#### #   .   # ####
#### # ## ## # ####
       #   #
#### # ## ## # ####
#### #       # ####
#### # ##### # ####
# . A.B .#        #
#.## ### # ###.## #
#! #           #  #
## # # ##### # # ##
#    #   #   #    #
# ###### # ###### #
#                 #
###################
"""
}

def getA() {
  return new Greedy()
}

def getB() {
    return new Layzie()
}