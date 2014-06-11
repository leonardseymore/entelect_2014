import za.co.entelect.challenge.agents.Dummie
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
#### #       # ####
#### #.## ## # ####
       #   #   B
#### # ## ## # ####
#### #    !  # ####
#### # ##### # ####
#     .  #        #
#.##.### # ###.## #
#A #     .     #  #
## # # ##### # # ##
#    #   #   #    #
# ###### # ###### #
#                 #
###################
"""
}

def getA() {
  return new Layzie()
}

def getB() {
    return new Dummie()
}