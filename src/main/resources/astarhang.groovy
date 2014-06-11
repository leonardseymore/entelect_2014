import za.co.entelect.challenge.agents.Dummie
import za.co.entelect.challenge.agents.Layzy

def getMap() {
  return """###################
#        #        #
# ## ### # ###.## #
# ## ### # ###.## #
#              .. #
#.## # ##### # ## #
#... #   #   #    #
#### ### # ### ####
#### #       # ####
#### # ## ## # ####
     . #   #   B
#### # ## ## # ####
#### #       # ####
#### #.##### # ####
#        #        #
# ## ### # ### ## #
#A.#           #  #
##.#.# ##### #.# ##
#....#...#   #..  #
#.######.# ###### #
#.........        #
###################
"""
}

def getA() {
  return new Layzy()
}

def getB() {
    return new Dummie()
}