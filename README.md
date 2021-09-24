# ServerCore
A fabric mod that aims to optimize & add multiplayer features to the minecraft server.\
This includes some ports of PaperMC (and forks) patches.

**Warning!**\
Some changes might be detectable by very complex redstone machines.\
Some changes are still WIP and may be unstable in specific situations.

**Current features:**
- Dynamic performance checks (changes settings depending on the mspt).
- Adjustable chunk-ticking distance - Allows for having high render distances at low costs.
- Adjustable mobcaps, item / xp merge radius & entity breeding limits.
- Option to slow down tick rates of villagers trapped in 1x1 spaces.
- Some optimizations and fixes for advancements, maps, entity navigation, chunk ticking and mob spawning.

**Planned features:**
- Entity Activation Range (Port from Spigot / PaperMC).

**Commands:**
- /servercore <name> <value> - Allows for modifying settings ingame.
- /mobcaps - Displays the current per-player mobcap for the user.
- /sc tps - Gives information about the current settings.

Most features are disabled by default and can be found in the config.\
The config file can be found at `<server_dir>/config/servercore.toml`
```toml
# Configuration for ServerCore - Fabric

[features] # Lets you enable / disable certain features and modify them.
    # Stops the server from loading spawn chunks.
    disable_spawn_chunks = false
    # Allows xp orbs to merge with others that have different experience amounts.
    # This will also allow players to pickup xp much faster.
    fast_xp_merging = false
    # Optimizes vanilla's per-player mobspawning by using PaperMC's PlayerMobDistanceMap.
    # Note: this might slightly increase memory usage.
    use_distance_map = false
    # Makes villagers tick less often if they are stuck in a 1x1 space.
    # The tick interval decides the interval between villager ticks when lobotomized.
    lobotomize_villagers = false
    lobotomized_tick_interval = 20
    # The amount of minutes in between auto-save intervals when /save-on is active.
    autosave_interval_minutes = 5
    # Decides the radius in blocks that items / xp will merge at.
    item_merge_radius = 0.5
    xp_merge_radius = 0.5

[entity_limits] # Stops animals / villagers from breeding if there are too many of the same type nearby.
    enabled = false
    # Count = maximum count before stopping entities of the same type from breeding.
    villager_count = 24
    animal_count = 32
    # Range = the range it will check for entities of the same type.
    villager_range = 64
    animal_range = 64

[dynamic] # Modifies mobcaps, no-chunk-tick, simulation and view-distance depending on the MSPT.
    enabled = false
    # The maximum and minimum permitted values.
    # Chunk tick distance = distance in which random ticks and mobspawning can happen.
    max_chunk_tick_distance = 10
    min_chunk_tick_distance = 2
    # Simulation distance = distance in which the world will tick, similar to no-tick-vd.
    max_simulation_distance = 10
    min_simulation_distance = 2
    # View distance = distance in which the world will render.
    max_view_distance = 10
    min_view_distance = 2
    # Mobcap = global multiplier that decides the percentage of the mobcap to be used.
    max_mobcap = 1.0
    min_mobcap = 0.3

[messages] # Allows you to modify the way some of the messages are formatted. Use the § symbol for color codes.
    # The title for the /mobcaps command.
    # Arguments: The current global mobcap modifier (%.1f).
    mobcap_title = "§3Per Player Mobcaps (§a%.1f§3)"
    # The content for the /mobcaps command. This is displayed for every existing spawngroup.
    # Arguments: The name of the spawngroup, the current mobcount near the player and the total capacity.
    mobcap_spawngroup = "§8- §3%s: §a%d §8/ §a%d"
```

# Setup
1. Navigate to https://github.com/Wesley1808/ServerCore-Fabric/actions
2. Select the topmost workflow run.
3. Download "ServerCore" from below **Artifacts**.
4. Unzip the file and grab the .jar file **without** -dev or -sources.
