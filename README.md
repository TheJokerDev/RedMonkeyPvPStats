# RedMonkeyPvPStats
A simple plugin to show the stats of a player in a scoreboard.

## Commands
- `/stats reload` - Reloads the config file.
- `/stats clearstats <player> <world> <kill/death/killstreak/all>` - Shows the stats of a player.

## Placeholders
> Statistics of a player in a world
- `%redmonkey_kills_{world}%`
- `%redmonkey_deaths_{world}%`
- `%redmonkey_killstreak_{world}%`
- `%redmonkey_bestkillstreak_{world}%`
- `%redmonkey_kdr_{world}%`

> Global statistics of a player
- `%redmonkey_kills_global%`
- `%redmonkey_deaths_global%`
- `%redmonkey_killstreak_global%`
- `%redmonkey_bestkillstreak_global%`
- `%redmonkey_kdr_global%`

## Config
```yaml
settings:
  prefix: '&c&lPVP&e&lStats &8» &7'
  debug: false

database:
  type: yaml # mysql, mongodb, sqlite, yaml
  mysql:
    host: localhost:3306
    database: pvpstats
    user: root
    password: 12345678
  mongodb:
    host: localhost:27017
    database: pvpstats
    user: root
    password: 12345678
    uri: 'mongodb://localhost:27017' #use this instead of host, user and password

rewards:
  example:
    worlds:
      - 'world'
    execute:
      kill:
        '*':
          - 'tell {victim} &cYou have been killed by {killer}!'
          - 'tell {killer} &aYou have been rewarded for killing {victim}!'
        '1':
          - 'give {victim} diamond 1'
      death:
        '*':
          - 'tell {player} &cYou died!'
        '5':
          - 'give {player} golden_apple 5'
          - 'give {player} book 1'
```

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Credits
- [RedMonkey](Luisito#8539) - Client
- [TheJokerDev](TheJokerDev#0001) - Developer