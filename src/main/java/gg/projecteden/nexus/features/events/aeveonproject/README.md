# Aeveon Project Notes

#### Resource Pack:

Download: http://dl.projecteden.gg/tp/AeveonProject.zip

To Overwrite Pack:
 1. sftp as root: /srv/http/dl.projecteden.gg/tp/
 2. ssh: web && cd dl.projecteden.gg && sudo chown www-data:www-data . -R

World/region based plugin:
- (Optional) https://www.spigotmc.org/resources/world-resourcepacks.18950/
- (Forced) https://www.spigotmc.org/resources/force-resourcepacks.10499/

---

### TODO:
- Resource pack textures
- PDA - Iron Pressure Plate, includes current objectives, Menu like quest menu on valeria
- Directional compass boss bar
- Radius based armorstand names for NPCs
    - Armorstands assigned to NPC names that mimic the NPCs nameplate, but disappear clientside
    - https://www.spigotmc.org/threads/spawning-in-a-clientside-nametag-using-armor-stands-protocollib-packets.371934/
    - Only toggle the armorstands name on if the player is nearby + has "met" the NPC
    - Armorstand data: gravity=deny, invulnerable=true, equipment=locked, size=small, visible=false
- Wind:
    - what if the wind blowing inside sound was playing all the time, and then when you go outside,
    it stacks the higher pitched sound on top, so there is a better fade
    and since the lower pitched sound is longer, and the higher is shorter, 
    I could even shorten how often the outside sound is repeated, resulting in a faster fad

