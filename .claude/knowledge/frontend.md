# Frontend

The frontend is a separate project at `../happy-marathon-ui/` (Vue 3 + Element Plus + TypeScript).

## Game module frontend files

```
happy-marathon-ui/src/
├── api/system/game/index.ts              # GameVO, GameSimpleVO, API functions
├── api/system/gameRegistration/index.ts  # GameRegistrationVO, API functions
├── views/system/game/index.vue           # Game list page
├── views/system/game/GameForm.vue        # Game create/edit dialog
├── views/system/gameRegistration/index.vue
├── views/system/gameRegistration/GameRegistrationForm.vue
└── utils/dict.ts                         # DICT_TYPE constants (lines 337-342)
```

The game list page (`index.vue`) uses `dict-tag` components to render dictionary values as colored tags. Filter fields: name (input), gameDate (date range picker), gameType (select from dict), status (select from dict).
