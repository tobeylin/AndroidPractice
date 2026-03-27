# How This Project Was Built

## Overview
PlaygroundWeather is a modern Android weather application demonstrating clean architecture and AI-assisted development.
For tech stack, architecture, and build commands, see [README.md](README.md).

## Development Tools
| Tool | Role | Notes |
| :--- | :--- | :--- |
| Android Studio Panda 2 | Primary IDE | Android development environment |
| Google Stitch | UI design prototyping | Designed screens via web UI; exported DESIGN.md, screen PNGs, and HTML |
| OpenCode (Claude Opus 4.6) | Planning & orchestration | Breaks down features into phased plans; coordinates implementation |
| OpenCode (Claude Sonnet 4.6) | AI code generation | Implements plans; generates Kotlin code guided by AGENTS.md |

## Development Workflow
The development followed a 6-phase structured flow, utilizing parallel workstreams where possible:

Phase 0 → Phase 1 → Phase 2 ⇄ Phase 3 → Phase 4 → Phase 5
         (Phases 2 & 3 ran in parallel)

- Phase 0 – UI Design (Stitch): Produced screen designs and the DESIGN.md design system.
- Phase 1 – Foundation: Created the compilable multi-module skeleton with Hilt DI and Material 3 theme.
- Phase 2 – Data Pipeline: Implemented network DTOs, repositories, DTO→model mapping, and unit tests.
- Phase 3 – UI Implementation: Developed Composable screens, ViewModels, and UiState in parallel with Phase 2.
- Phase 4 – Integration: Wired end-to-end data flow, error/loading states, and handled edge cases.
- Phase 5 – Polish: Added i18n support (zh-rTW), weather backgrounds, sunrise/sunset, and finalized README.

Stats: 83 commits over 4 days using conventional commit format.

## AI-Assisted Development
**Google Stitch** produced 9+ screen designs via its web UI, along with a DESIGN.md color/typography spec and per-screen PNG and HTML exports. These served as the visual reference for the UI implementation without manual Figma work.

**OpenCode (Claude)** generated Kotlin code for all 6 modules, wrote comprehensive unit tests, and managed the multi-module Gradle configuration. It operated under strict AGENTS.md guardrails, ensuring no XML layouts were used and all dependencies were managed via the version catalog.

**AGENTS.md** serves as the 325-line AI coding contract that defines architecture rules, code style, testing strategy, and guardrails. It acts as the single source of truth to maintain high code quality and consistency across all AI-generated components. Link: [AGENTS.md](AGENTS.md)

## Related Documentation
- [README.md](README.md) — Tech stack, architecture, module graph, build commands, setup
- [AGENTS.md](AGENTS.md) — AI coding contract: architecture rules, code style, guardrails
- [docs/phase-plan.md](docs/phase-plan.md) — Full 6-phase development plan with risks and decisions (Chinese)
- [docs/stitch_playgroundweather/](docs/stitch_playgroundweather/) — Stitch UI design outputs (screen screenshots, HTML exports)
