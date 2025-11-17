# RailDest

RailDest is a plugin that enhances rail mechanics, allowing for automated
routing
across rail lines according to a specified destination. The plugin uses the
redstone mechanics of a T-junction that follows the southeast rule and interacts
with detector rails.

# Usage

`/dest place` where `place` is the place where you want to go. You can also
chain
destinations `/dest place1 place2` etc. this can be useful for taking a route
via specific junctions, RailDest will route players towards signs that contain
either `place1` or `place2`.

# Junction setup

A sign with the destination name on it must be placed over a detector rail
like in the image shown below.
The sign's first line must be `[destination]`, then the other three lines can be
destination names. You may also
use `[!destination]`, which will activate the rail if a player's destination is
not on the signs.

![Junction example](https://static.wikitide.net/civwikiwiki/thumb/d/d6/RailSwitchSideVision.png/800px-RailSwitchSideVision.png)
![Junction example from above](https://static.wikitide.net/civwikiwiki/thumb/3/37/RailSwitchFromAboveText.png/800px-RailSwitchFromAboveText.png)

If the signal needs to be inverted, an invertor could be used as such shown
below:

![Junction invertor](https://static.wikitide.net/civwikiwiki/thumb/8/81/RailSwitchInvertor.png/800px-RailSwitchInvertor.png)