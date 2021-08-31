# resource-spawner
A Bukkit plugin for minecraft 1.16 that randomly generates temporary structures into an existing world

## What ResourceSpawner does

### The short version
ResourceSpawner spawns things at random locations, and optionally decays them after an amount of time

<details>
<summary><b>The long version</b></summary>
Each cycle, each Resource Spawner will:

- If the structure limit is not met:
- Choose a World Provider, and use it to pick a world.
- Choose a Location Provider, and use it to pick a location.
- Choose a Spawn Provider, and check a set of Conditions.
- If all conditions are met, use it to spawn something.
### World Providers
World providers provide a list of worlds a Resource Spawner may operate in.
### Location Providers
Location providers provide an area a Resource Spawner may operate in, such as a cuboid, sphere, etc.
Some location providers offer Distributions, to allow generation bias towards certain areas
### Spawn Providers
Spawn providers spawn something in the location, such as a structure or entity
#### Structure Providers
Structure Providers are a special type of Spawn Provider that will spawn a structure. Since structure generation speed is limited, you can choose a Structure Sorter to define in what order the blocks will spawn or decay

Structure Providers allow you to schedule the structure to decay after a given amount of time. This time can be reset by Triggers
#### Triggers
Triggers will reset the timer on a structure provider when a specific event happens, such as a block being broken or a player moving nearby. Triggers can be set to reset the time to any value.
### Conditions
Conditions check the area around a spawn for specific features before allowing it to continue.

### For even more details, see the **Features List** section
</details>

## Lag-free
All significant actions in ResourceSpawner, from scanning a large area to spawning a single entity, are limited to spend no more than a given amount of time per tick. This is configurable, in nanoseconds, for each resource spawner
In addition, resource spawners can be set to only tick at specific intervals

## Extensive Plugin API

Plugins can register new World Providers, Location Providers, Spawn Providers, Conditions, Structure Sorters, Triggers, and Distributions by listening for the [`ResourceSpawnerInitializationEvent`](https://github.com/ThizThizzyDizzy/resource-spawner/blob/master/src/com/thizthizzydizzy/resourcespawner/ResourceSpawnerInitilizationEvent.java) and using its `register` methods.

For examples of these, see the [Source Code](https://github.com/ThizThizzyDizzy/resource-spawner/blob/master/src/com/thizthizzydizzy/resourcespawner/ResourceSpawnerCore.java#L293)

*Note: Your plugin should use itself for the `NamespacedKey`, not ResourceSpawner*

## [Hjson](https://hjson.github.io/) Configuration
ResourceSpawner uses Hjson for its configuration to avoid the messiness of YAML, while still providing a reasonably user-friendly configuration file
### Basic configuration format:
<details>
<summary><b>Basic configuration format:</b></summary>

```
{
    debug: false //this line is optional, but set it to true to enable an excessive amount of console output for debugging (This may slow down the plugin)
    resource_spawners: [
        {
            //a resource spawner
        }
        {
            //another resource spawner
        }
    ]
}
```
</details>
For the formats of Resource Spawners and everything else, see the **Features List** section

## Features List
Here is a comprehensive list of everything ResourceSpawner has to offer.
All settings are Optional unless otherwise specified
### Resource Spawners
<details>
<summary><b>Settings</b></summary>

String **`name` (Required)**

The name of the resource spawner, used for saving/loading and for debugging. Must be unique

Array **`world_providers`**

Each spawn, a world provider will be randomly chosen from this list.

Although not required, this resource spawner will do nothing if this is not present

Array **`location_providers`**

Each spawn, a location provider will be randomly chosen from this list.

Although not required, this resource spawner will do nothing if this is not present

Array **`spawn_providers`**

Each spawn, a spawn provider will be randomly chosen from this list.

Although not required, this resource spawner will do nothing if this is not present

int **`limit`** (Default 1)

The maximum number of structures this resource spawner can have active at a time. Set to -1 to disable the limit, but be very careful if you do to avoid runaway generation!

Note: This only counts Structures, not other spawn types!

int **`spawn_delay`** (Default 0)

The minimum delay, in ticks, between spawns. (This is the time from the beginning of one spawn to the begginning of the next, although there cannot be multiple things spawning at the same time in one resource spawner)

int **`tick_interval`** (Default 1)

The interval, in ticks, at which this resource spawner runs. This is useful if you don't want it running every tick. (This will decrease the resolution of all underlying delays; ex. if you set this to 1 minute, delays will be effectively rounded up to the nearest minute)

long **`max_tick_time`** (Default 5_000_000 (5ms, or 10% of a tick))

The maximum time, in nanoseconds, this resource spawner may spend on a task in one tick. (Resolution is not perfect; see [System#nanoTime()](https://docs.oracle.com/javase/8/docs/api/java/lang/System.html#nanoTime()) if you're interested in why)
</details>
<details>
<summary><b>Example</b></summary>

```
{
    name: example
    world_providers: [
        {
            //a world provider
        }
    ]
    location_providers: [
        {
            //a location provider
        }
    ]
    spawn_providers: [
        {
            //a spawn provider
        }
    ]
    limit: 10 //maximum of 10 structures
    spawn_delay: 1200 //spawn every minute (1200 ticks)
    tick_interval: 200 //tick every 10 seconds (200 ticks)
    max_tick_time: 1_000_000 //limit of 1 millisecond per tick (1,000,000 nanoseconds)
}
```
</details>

### World Providers
<details>
<summary><b>Settings common to all world providers</b></summary>

String **`type` (Required)**

Which world provider this is; should be a [namespaced ID](https://minecraft.fandom.com/wiki/Resource_location)

If no namespace is provided, a default of `resourcespawner` will be assumed

int **`weight`** (Default 1)

Determines the chance of this world provider being picked. Works like the weights in [Loot Tables](https://minecraft.fandom.com/wiki/Loot_table)
</details>

#### List of world providers

<details>
<summary><b>Environment</b></summary>

id: `resourcespawner:environment`

Provides a random world with a certain [Environment](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/World.Environment.html)

All worlds have an equal chance of being selected; For individual weights, make a World Provider for each one

##### Settings

String **`environment`** (Default normal)

The environment to provide. Accepted values are all environments [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/World.Environment.html), as well as `overworld` for the `normal` environment

##### Example

```
{
    type: environment //namespace not required for built-in stuff
    weight: 1 //common settings
    environment: nether //provide all nether-type worlds
}
```
</details>
<details>
<summary><b>UUID</b></summary>

id: `resourcespawner:uuid`

Provides a set of worlds by their UUIDs

All worlds have an equal chance of being selected; For individual weights, make a World Provider for each one

##### Settings

Array **`worlds`** (Default normal)

A list of worlds to provide. Each entry must be a string with the String representation of the world's UUID (with dashes!)

boolean **`blacklist`** (Default false)

If set, this will provide all worlds *except* those provided in `worlds`

##### Example

```
{
    type: uuid //namespace not required for built-in stuff
    weight: 1 //common settings
    worlds: [
        "6943ac80-a52f-4a42-90ed-c9223bfa75f8" //a world UUID
        "01234567-89ab-cdef-0123-456789abcdef" //another world UUID
    ]
    blacklist: true //provide all worlds except these
}
```
</details>
<details>
<summary><b>Name</b></summary>

id: `resourcespawner:name`

Provides a set of worlds by their Names

All worlds have an equal chance of being selected; For individual weights, make a World Provider for each one

##### Settings

Array **`worlds`** (Default normal)

A list of worlds to provide. Each entry must be a string with the world's name (Case sensitive!)

boolean **`blacklist`** (Default false)

If set, this will provide all worlds *except* those provided in `worlds`

##### Example

```
{
    type: name //namespace not required for built-in stuff
    weight: 1 //common settings
    worlds: [
        some_amazing_world //a world name
        another_less_amazing_world //another world name
    ]
    blacklist: false //provide only these worlds
}
```
</details>

### Location Providers
<details>
<summary><b>Settings common to all location providers</b></summary>

String **`type` (Required)**

Which location provider this is; should be a [namespaced ID](https://minecraft.fandom.com/wiki/Resource_location)

If no namespace is provided, a default of `resourcespawner` will be assumed

int **`weight`** (Default 1)

Determines the chance of this location provider being picked. Works like the weights in [Loot Tables](https://minecraft.fandom.com/wiki/Loot_table)

</details>

#### List of location providers

<details>
<summary><b>Block</b></summary>

id: `resourcespawner:block`

Provides the location of a single block

##### Settings

int **`x` (Required)**

The X coordinate of the block

int **`y` (Required)**

The Y coordinate of the block

int **`z` (Required)**

The Z coordinate of the block

##### Example

```
{
    type: block //namespace not required for built-in stuff
    weight: 1 //common settings
    x: 42
    y: 64
    z: 9001
}
```
</details>

<details>
<summary><b>Square</b></summary>

id: `resourcespawner:square`

Provides random locations in a square around a point in the world

##### Settings

int **`x` (Required)**

The X coordinate of the center of the square

int **`z` (Required)**

The Z coordinate of the center of the square

int **`radius`** (default 0)

The radius of the square, not including the center block (radius of 1 means 3x3, 2 means 5x5, etc.)

int **`min_y`** (default -2147483648)

The minimum Y value of this cuboid. Locations will never be provided lower than the bottom of the world, so negative values are allowed

int **`max_y`** (default 2147483647)

the maximum Y value of this cuboid. Locations will never be provided higher than the top of the world, so extremely large values are allowed

String **`vertical_distribution`** (default even)

The vertical distribution to use. should be a namespaced id matching that of a Distribution (see **Distributions**)

String **`horizontal_distribution`** (default even)

The horizontal distribution to use. should be a namespaced id matching that of a Distribution (see **Distributions**)

##### Example

```
{
    type: square //namespace not required for built-in stuff
    weight: 1 //common settings
    x: 42 //center
    z: 42 //center
    radius: 500 //offer a 500 block radius (1001x1001) square
    min_y: 64 // all locations above sea level
    max_y: 1024 // no locations higher than 1024
    vertical_distribution: even //use an even distribution for vertical distribution
    horizontal_distribution: gaussian //use a gaussian distribution for horizontal distribution
}
```
</details>

<details>
<summary><b>Circle</b></summary>

id: `resourcespawner:circle`

Provides random locations in a circle around a point in the world

Note: This has the same distribution as the Square Location provider, but discards any points outside the circle

##### Settings

int **`x` (Required)**

The X coordinate of the center of the circle

int **`z` (Required)**

The Z coordinate of the center of the circle

int **`radius`** (default 0)

The radius of the circle

int **`min_y`** (default -2147483648)

The minimum Y value of the cylinder. Locations will never be provided lower than the bottom of the world, so negative values are allowed

int **`max_y`** (default 2147483647)

the maximum Y value of the cylinder. Locations will never be provided higher than the top of the world, so extremely large values are allowed

String **`vertical_distribution`** (default even)

The vertical distribution to use. should be a namespaced id matching that of a Distribution (see **Distributions**)

String **`horizontal_distribution`** (default even)

The horizontal distribution to use. should be a namespaced id matching that of a Distribution (see **Distributions**)

##### Example

```
{
    type: circle //namespace not required for built-in stuff
    weight: 1 //common settings
    x: -64 //center
    z: 9001 //center
    radius: 400 //offer a 400 block radius circle
    min_y: 0 // all locations above y=0
    max_y: 255 // no locations higher than 255
    vertical_distribution: gaussian //use a gaussian distribution for vertical distribution
    horizontal_distribution: even //use an even distribution for horizontal distribution
}
```
</details>

<details>
<summary><b>Cuboid</b></summary>

id: `resourcespawner:cuboid`

Provides random locations from a given cuboid area

##### Settings

int **`min_x` (Required)**

the minimum X value of the cuboid

int **`max_x` (Required)**

the maximum X value of the cuboid

int **`min_y`** (default -2147483648)

The minimum Y value of the cuboid. Locations will never be provided lower than the bottom of the world, so negative values are allowed

int **`max_y`** (default 2147483647)

the maximum Y value of the cuboid. Locations will never be provided higher than the top of the world, so extremely large values are allowed

int **`min_z` (Required)**

the minimum Z value of the cuboid

int **`max_z` (Required)**

the maximum Z value of the cuboid

String **`x_distribution`** (default even)

The distribution to use on the X axis. should be a namespaced id matching that of a Distribution (see **Distributions**)

String **`y_distribution`** (default even)

The distribution to use on the Y axis. should be a namespaced id matching that of a Distribution (see **Distributions**)

String **`z_distribution`** (default even)

The distribution to use on the Z axis. should be a namespaced id matching that of a Distribution (see **Distributions**)

##### Example

```
{
    type: cuboid //namespace not required for built-in stuff
    weight: 1 //common settings
    min_x: 64
    max_x: 128
    min_y: 32
    max_y: 512
    min_z: 2
    max_z: 9999
    x_distribution: gaussian //use a gaussian distribution for the X axis
    y_distribution: even //use an even distribution for the Y axis
    z_distribution: even //use an even distribution for the Z axis
}
```
</details>

<details>
<summary><b>Surface</b></summary>

id: `resourcespawner:surface`

Provides random locations from the surface in a given square

Initial locations are the highest solid block at each x/z value, as described in [World#GetHighestBlockAt](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/World.html#getHighestBlockAt(int,int))

##### Settings

int **`x` (Required)**

The x coordinate of the center of the square

int **`z` (Required)**

The z coordinate of the center of the square

int **`y_offset`** (Default 1)
The height off the ground that is the location provided
if set to 0, the ground block itself will be provided.

int **`radius`** (Default 0)

The radius of the square, not including the center block (radius of 1 means 3x3, 2 means 5x5, etc.)

String **`distribution`** (default even)

The distribution to use. should be a namespaced id matching that of a Distribution (see **Distributions**)

##### Example

```
{
    type: surface //namespace not required for built-in stuff
    weight: 1 //common settings
    x: 96
    z: 1000000
    radius: 1000
    distribution: gaussian //use a gaussian distribution
}
```
</details>

### Spawn Providers
<details>
<summary><b>Settings common to all spawn providers</b></summary>

String **`type` (Required)**

Which spawn provider this is; should be a [namespaced ID](https://minecraft.fandom.com/wiki/Resource_location)

If no namespace is provided, a default of `resourcespawner` will be assumed

int **`weight`** (Default 1)

Determines the chance of this location provider being picked. Works like the weights in [Loot Tables](https://minecraft.fandom.com/wiki/Loot_table)

Array **`conditions`** (Optional)

A list of Conditions that must be met for this to spawn. See **Conditions** for more details

</details>

Structure spawn providers are a type of spawn provider that generate structures. They also have some common settings:

<details>
<summary><b>Settings common to all structure spawn providers</b></summary>

String **`name` (Required)**

The name of the spawn provider, used for saving/loading and for debugging. Must be unique

String **`build_order`**

The order in which blocks will spawn. should be a namespaced id matching that of a Structure Sorter (see **Structure Sorters**)

If no namespace is provided, a default of `resourcespawner` will be assumed

If no build order is listed, the structure will be built in an undefined order

Array **`replace`**

A list of blocks that can be replaced. 

Each entry must be a string, which can match a block name or block tag (ex. #minecraft:fences)

Object **`decay`**

An object that holds all of the decay settings.

##### Decay settings

String **`decay_order`**

The order in which blocks will decay. should be a namespaced id matching that of a Structure Sorter (see **Structure Sorters**)

If no namespace is provided, a default of `resourcespawner` will be assumed

If no decay order is listed, the structure will decay in an undefined order

int **`delay` (Required)**

The time, in ticks, before this structure will decay

Array **`reset_triggers`**

A list of Triggers that can extend the decay time. see **Triggers** for more details

String **`decay_to`** (Default air)

The block to decay to. (ex. air or water)

##### Trigger settings

Each Decay trigger has these common settings

int **`delay` (Required)**

The value to reset the decay timer to. This cannot decrease the time

</details>

#### List of Structure spawn providers

<details>
<summary><b>WorldEdit Schematic</b></summary>

id: `resourcespawner:we_schematic`

(Requires [WorldEdit](https://dev.bukkit.org/projects/worldedit))

Provides a structure from a schematic readable by WorldEdit

The structure will always spawn with the center of the structure at the spawn location.

##### Settings

String **`file` (Required)**

The path to the schematic file, from the ResourceSpawner folder (a value of `folder/file.schem` would point to the file `.../plugins/ResourceSpawner/folder/file.schem`)

##### Example

```
{
    type: we_schematic //namespace not required for built-in stuff
    name: wow_so_cool
    weight: 1 //common settings
    file: some/interesting/subfolder/cool_structure.schem
    conditions: [
        {
            //a condition
        }
        {
            //another condition
        }
    ]
    build_order: from_center //build from center, namespace not required for built-in stuff
    replace: [//replace only air and cave air
        air
        cave_air
    ]
    decay: {
        decay_order: random //decay in random order, namespace not required for built-in stuff
        delay: 72000 //decay after 1 hour
        reset_triggers: [
            {
                //trigger stuff here
                delay: 12000 //reset to 10 minutes
                conditions: [
                    {
                        //a condition
                    }
                    {
                        //another condition
                    }
                ]
            }
        ]
        decay_to: air //leave air behind when decaying
    }
}
```
</details>

#### List of Other spawn providers

<details>
<summary><b>Entity</b></summary>

id: `resourcespawner:entity`

Spawns a single entity of the given type

##### Settings

String **`entity` (Required)**

The entity type to spawn

##### Example

```
{
    type: entity //namespace not required for built-in stuff
    weight: 1 //common settings
    entity: minecraft:sheep
    conditions: [
        {
            //a condition
        }
        {
            //another condition
        }
    ]
}
```
</details>

### Conditions
<details>
<summary><b>Settings common to all conditions</b></summary>

String **`type` (Required)**

Which condition this is; should be a [namespaced ID](https://minecraft.fandom.com/wiki/Resource_location)

If no namespace is provided, a default of `resourcespawner` will be assumed

</details>

#### List of conditions

<details>
<summary><b>Cube Fill</b></summary>

id: `resourcespawner:cube_fill`

Checks the block types in a cube centered on the spawn location

##### Settings

int **`radius`** (Default 0)

The radius of the cube, not including the center block (radius of 1 means 3x3x3, 2 means 5x5x3, etc.)

If the cube extends below the bottom of the world or past world height, every position outside the world will be skipped

Array **`blocks` (Required)**

A list of blocks to search for 

Each entry must be a string, which can match a block name or block tag (ex. #minecraft:fences)

int **`min`**

The minimum number of blocks that must be present. If fewer than this many blocks are found, the condition will fail

int **`max`**

The maximum number of blocks that must be present. If more than this many blocks are found, the condition will fail

double **`min_percent`**

The minimum percentage of blocks that must be present (where 100.0 is 100%) If fewer than this percentage of the total area are the given blocks, the condition will fail

double **`max_percent`**

The maximum percentage of blocks that must be present (where 100.0 is 100%) If more than this percentage of the total area are the given blocks, the condition will fail

##### Example

```
{
    type: cube_fill //namespace not required for built-in stuff
    radius: 200 //check all blocks in a 401x401x401 cube
    blocks: [
        air //only looking for air, nothing else
    ]
    //you probably don't need all four of these at once; they're just here for demonstration
    min: 50 // must have at least 50 air blocks
    max: 500 // must have no more than 500 air blocks
    min_percent: 100 //must be all air
    max_percent: 20 //must be no more than 20% air
}
```
</details>

<details>
<summary><b>Cube WorldGuard Region</b></summary>

id: `resourcespawner:cube_wg_region`

(Requires [WorldGuard](https://dev.bukkit.org/projects/worldguard))

Checks for WorldGuard regions in a cube centered on the spawn location (to check just the spawn block, set radius to 0)

##### Settings

int **`radius`** (Default 0)

The radius of the cube, not including the center block (radius of 1 means 3x3x3, 2 means 5x5x3, etc.)

If the cube extends below the bottom of the world or past world height, every position outside the world will be skipped

Array **`regions`**

A list of regions to search for 

Each entry must be a string, which matches the region ID (name)

boolean **`invert`** (Default false)

If set to true, this will look for any region *except* those listed in `regions`

##### Example

```
{
    type: cube_wg_region //namespace not required for built-in stuff
    radius: 200 //check all blocks in a 401x401x401 cube
    regions: [
        donotspawnstuffhere //the do not spawn stuff here region
        spawn //the spawn region
    ]
    invert: true //don't spawn stuff in the above regions
}
```
</details>

<details>
<summary><b>Entity Proximity</b></summary>

id: `resourcespawner:entity_proximity`

Searches for entities in a spherical radius around the spawn location

##### Settings

int **`radius`** (Default 0)

The radius of the sphere to check

Array **`entities`**

A list of entity types to search for

Each entry must be a string, matching the id of an entity type

boolean **`invert`** (Default false)

If set to true, this will fail if any the listed entities are found

##### Example

```
{
    type: entity_proximity //namespace not required for built-in stuff
    radius: 200 //check for entities in a 200 block radius
    entities: [
        player //only spawn near players
        item //or items too, I guess
    ]
    invert: false //one of these entities must be nearby
}
```
</details>

<details>
<summary><b>Block</b></summary>

id: `resourcespawner:block`

Searches for a specific block in a specific relative location

##### Settings

int **`x_offset`** (default 0)

The X offset of the block to look for

int **`y_offset`** (default 0)

the Y offset of the block to look for

int **`z_offset`** (default 0)

the Z offset of the block to look for

boolean **`invert`** (default false)

if true, this condition will fail if the block matches any of those listed in `blocks`

Array **`blocks`**

A list of blocks to search for 

Each entry must be a string, which can match a block name or block tag (ex. #minecraft:fences)

##### Example

```
{
    type: block//namespace not required for built-in stuff
    x_offset: 0
    y_offset: -1//check the block just below
    z_offset: 0
    blocks: [
        lava//must not be lava
    ]
    invert: true //must not be
}
```
</details>

<details>
<summary><b>Biome</b></summary>

id: `resourcespawner:biome`

Checks the biome at the spawn location

##### Settings

boolean **`invert`** (default false)

if true, this condition will fail if the biome matches any of those listed in `biomes`

Array **`biomes`**

A list of biomes to search for 

Each entry must be a string, matching the biome name

##### Example

```
{
    type: block//namespace not required for built-in stuff
    biomes: [
        plains//only spawns in plains
    ]
    invert: false //only in plains
}
```
</details>

<details>
<summary><b>World Time</b></summary>

id: `resourcespawner:world_time`

Checks that the world time is within a specific range (current day time, not total time)

##### Settings

int **`min`**

The minimum time allowed

int **`max`**

The maximum time allowed

##### Example

```
{
    type: world_time//namespace not required for built-in stuff
    min: 6000 //must be after noon
    max: 18000 //must be before midnight
}
```
</details>

<details>
<summary><b>Moon Phase</b></summary>

id: `resourcespawner:moon_phase`

Checks that the phase of the moon is within a specific range, where 0 is the full moon

##### Settings

int **`min`**

The minimum phase allowed

int **`max`**

The maximum phase allowed

##### Example

```
{
    type: moon_phase//namespace not required for built-in stuff
    min: 3 //must be at least a waning crescent
    max: 5 //must be no later than a waxing crescent
}
```
</details>

### Structure Sorters

Structure sorters do not have individual settings

#### List of Structure Sorters

<details>
<summary><b>From Center</b></summary>

id: `resourcespawner:from_center`

Creates or destroys blocks from the center of the structure outwards
</details>

<details>
<summary><b>To Center</b></summary>

id: `resourcespawner:to_center`

Creates or destroys blocks from the outside of the structure inwards towards the center
</details>

<details>
<summary><b>Random</b></summary>

id: `resourcespawner:random`

Creates or destroys blocks in a random order
</details>

### Triggers

<details>
<summary><b>Settings common to all triggers</b></summary>

String **`trigger` (Required)**

Which trigger this is; should be a [namespaced ID](https://minecraft.fandom.com/wiki/Resource_location)

If no namespace is provided, a default of `resourcespawner` will be assumed

Array **`conditions`**

A list of Conditions that must be met for this trigger to activate. See **Conditions** for more details

</details>

#### List of triggers

<details>
<summary><b>Block Broken</b></summary>

id: `resourcespawner:block_broken`

Triggers when one of a structures' blocks is broken

##### Settings

List **`blocks`**

A list of blocks to trigger for. If not provided, this will trigger for all blocks.

Each entry must be a string, which can match a block name or block tag (ex. #minecraft:fences)

##### Example

```
{
    trigger: block_broken//namespace not required for built-in stuff
    blocks: [
        stone
        iron_ore
        light_blue_glazed_terracotta
    ]
}
```
</details>

<details>
<summary><b>Timer</b></summary>

id: `resourcespawner:timer`

Triggers at regular intervals

##### Settings

int **`interval` (Required)**

How often to trigger, in ticks

##### Example

```
{
    trigger: timer//namespace not required for built-in stuff
    interval: 20 //trigger every second
    conditions: [//timer is meant to be used with conditions
        //a condition
    ]
}
```
</details>

### Distributions

Distributions do not have individual settings

#### List of Distributions

<details>
<summary><b>Even</b></summary>

id: `resourcespawner:even`

provides a random number with an equal change of each value
</details>

<details>
<summary><b>Gaussian</b></summary>

id: `resourcespawner:gaussian`

provides a random number with a gaussian distribution, centered on the middle of the given range, with a standard deviation that meets both ends. **This can exceed the given range!**
</details>