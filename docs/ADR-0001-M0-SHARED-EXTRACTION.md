# ADR 0001: M0 shared-component extraction

Status: accepted for the FramedBlocks M0 checkpoint.

## Decision

Promote the following findings to portfolio specifications, but keep all
production source private to this repository for now:

- add-on entrypoints register block-entity DTOs without deserializing NBT;
- the first BlueNBT capability probe runs only after every add-on entrypoint,
  because BlueNBT 3.5.1 snapshots the possible DTO set on first use;
- internal BlueMap adapters use a minimal version bootstrap, disjoint registry
  keys, identity-verified registration, inactive hooks, and guarded stock
  fallback;
- a source-composite build is commit- and cleanliness-checked in settings
  before evaluating the included build;
- exact artifact and generated inventory checks are deterministic test/build
  inputs, not runtime resources copied from the candidate mod;
- bounded diagnostic reasons contain no coordinates, NBT, paths, or player
  data.

Do not create a shared runtime provider or extract shared production source at
this checkpoint. Only FramedBlocks implements these contracts, so the
two-implementation/third-consumer promotion gate is not met. The exact-profile
decoder and future geometry remain in the LGPL FramedBlocks repository.

## Consequences

The next AE2 implementation must challenge these specifications, especially
the global BlueNBT registration timing and fallback/load-order behavior. A
development-only toolkit may later host independently authored test helpers,
but FramedBlocks M0 does not yet justify a separately versioned toolkit
artifact or an installed dependency.
