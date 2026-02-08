# Quick Shulker
Quickly open a held shulker box with the press of a key!

## Config
Server config options stored in `quickshulker_config.json5`.
- `quickShulkerBox`: Enables quick opening shulker boxes
- `quickCraftingTable`: Enables quick opening crafting tables
- `quickEnderChest`: Enables quick opening ender chests
- `quickStonecutter`: Enables quick opening stonecutters
- `supportBundlingInsert`: Enable right-clicking a shulker with an item to insert it.
- `supportBundlingPickup`: Enable right-clicking an item with a shulker to insert it.
- `supportBundlingExtract`: Enable right-clicking an empty slot with a shulker to extract an item.

These are loaded on server *and client* start. In singleplayer these options are applied.
Malilib + Modmenu is required for configuring client options.

## Dev Notes

Forked from the last 1.15 commit in kyyrptonaught/quickshulker. 

Copied kyrptonaught/kyrptconfig into repo and tried to downgrade to 1.15 since the oldest
available version is for 1.17.

Very janky downgrade. Copied kryptconfig, brought in minimal files to compile.

```shell
# generate deps.json by mitm gradle
$(nix build .#quickshulker.mitmCache.updateScript --no-link --print-out-paths)
nix build
```
## Testing

Manually test:

- #2: Right clicks bleeding through inventory and interacting/placing shulker. Also test in water.
- #4: Stacked shulker boxes dupe (with carpetmod)
- #5: Shulker dupe after moving shulker box in inventory.
- #6: Shulker dupe with bleed-through placing and opening shulker at same time.
- #15: renamed shulker boxes should display the correct name.
- #26: Opening ender chest in opened ender chest destroys in-inventory ender chest.
- #35: Crash upon right-clicking destroy button in creative inventory.
- #36: stacked shulker boxes buggy
