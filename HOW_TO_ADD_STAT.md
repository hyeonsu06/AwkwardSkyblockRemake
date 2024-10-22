# How to add stat

### 1. Modify ItemStats.java, ItemAdditiveBonus.java and ItemMultiplicativeBonus.java in src/core/annotations/items/items/
### 2. Modify methods(baseStatLore(), additiveStatLore(), multiplicativeStatLore()) in Refresher.java in src/core/
### 3. Modify methods(applyStat(), applyAccessoryStat(), applyReforgeStat()) to apply stats
### 4. Modify EntityManager.java to make stats work
### 5. (If stat affects player) modify onJoin Eventhandler to assign default base value

...and that's literally all