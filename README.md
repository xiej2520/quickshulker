# Quick Shulker
Quickly open a held shulker box with the press of a key!

## Dev Notes

Forked from the last 1.15 commit in kyyrptonaught/quickshulker. 

Copied kyrptonaught/kyrptconfig into repo and tried to downgrade to 1.15 since the oldest
available version is for 1.17.

Very janky downgrade. Copied kryptconfig, brought in minimal files to compile.

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
