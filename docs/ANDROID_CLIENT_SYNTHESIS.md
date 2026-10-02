# Open-source Android clients for NousResearch Hermes Agent

Research snapshot: 2026-10-02. This repository is a **fork of [Hy4ri/hermes-mobile](https://github.com/Hy4ri/hermes-mobile)**, not a wholesale merge of unrelated apps. The working app is the upstream Apache-2.0 native Kotlin/Compose dashboard client. The comparison below identifies what is already integrated and what still needs original implementation and testing. Other clients are research references; their code is **not** included.

## Why this foundation

Hy4ri's app already combines the highest-value mobile workflows in a single build: dashboard-authenticated chat and local history, REST management screens, JSON-RPC/WebSocket streaming, explicit approval/clarification UI, attachment handling, voice notes, multi-server profiles, background notifications and reply, session recovery, model picking, themes, and a substantial Android CI/test suite. Its Apache-2.0 license permits an attributed fork. Do not mistake breadth for device-tested interoperability: compatibility depends on the running Hermes dashboard version and authentication configuration.

## Source comparison and synthesis

| Client | License / integration surface | Valuable feature or lesson | Status in this repository |
| --- | --- | --- | --- |
| [Hy4ri/hermes-mobile](https://github.com/Hy4ri/hermes-mobile) | Apache-2.0; native Kotlin/Compose; dashboard REST + `/api/ws` | Chat, Room history, sessions, cron, skills, profiles, tools, logs, notifications, approval UI, images, voice notes, reconnect | **Included** as attributed fork; see [README](../README.md) and `app/src/main/java/com/m57/hermescontrol/` |
| [Codename-11/hermes-relay](https://github.com/Codename-11/hermes-relay) | MIT; dashboard/gateway and optional Relay plugin | Full voice and consent-gated device integrations | **Not included**: optional server plugin and privacy/permission boundaries require a separate design and tests |
| [cogwheel0/conduit](https://github.com/cogwheel0/conduit) | GPL-3.0; Flutter; Hermes API/server adapter | Cross-platform UX, schedules, visible tool activity and sensitive-step approval | **Concept reference only**: approval, schedule and tool surfaces already exist here; copying GPL implementation into an Apache fork would impose different obligations |
| [onlyxn/hermes-friends](https://github.com/onlyxn/hermes-friends) | MIT; dashboard REST + `/api/ws` | Multiple servers and workspace-grouped conversations | **Included as original implementation**: multiple connections and an optional grouping chip for sessions matched to named backend project folders. Unmatched rows remain in Other sessions; no project data means an ordinary list. No code copied. |
| [a9ito/hermes-droid](https://github.com/a9ito/hermes-droid) | MIT; OpenAI-compatible API server | Lightweight scratch chat and capability-gated durable sessions | **Not included**: this app is dashboard-first; `/v1` API keys and dashboard cookies/tickets cannot be mixed |
| [karem505/hermes-mobile-remote-gateway](https://github.com/karem505/hermes-mobile-remote-gateway) | MIT; dashboard password login, cookie and single-use WS ticket | Reliable remote gateway authentication and realistic notification expectations | **Mostly included** via existing auth and ticket flow; background local notifications are not guaranteed remote push |
| [veficos/hermes-mobile](https://github.com/veficos/hermes-mobile) | MIT; custom FastAPI Mobile Server | Dedicated mobile server and WebSocket | **Not included**: `/api/v1/*` is *not* a stock Hermes route |
| [rusty4444/hermes-android](https://github.com/rusty4444/hermes-android) | No license grant found (see [NOTICE](https://github.com/rusty4444/hermes-android/blob/main/NOTICE.md)); gateway JSON-RPC | Projects, per-chat models and proposed turn recovery | **Behavior reference only**: do not copy source without permission; experimental `turn_recovery` is not a stock contract |
| [hermes-webui/hermes-android](https://github.com/hermes-webui/hermes-android) | No license grant found; WebView for separate Hermes WebUI | Trusted-origin navigation and Android share/file handling | **Not included**: requires a separate server and does not substitute for a native dashboard client |

A GitHub repository being public does not grant a license. All entries describe projects independently maintained by their authors, not official Nous Research Android releases. Meta's `facebook/hermes` is a JavaScript engine and unrelated.

## Recommended single-app feature set

- **Ship from the existing foundation:** native chat, reconnect/replay, local history, attachments and voice *notes*, approvals, session browsing, cron/skills/profiles, model selection, multiple server connections, and notifications. This fork also adds optional workspace grouping backed by named dashboard project folders. Verify behavior against the target Hermes revision; local tests and CI do not prove a real phone can reach a live gateway.
- **Next: project UX refinement:** persist grouping preference, reconcile paginated history and support project management only where backend capabilities are confirmed. Never infer a workspace from text labels alone.
- **Next: optional full-duplex voice:** only behind explicit microphone consent; no background microphone or cloud relay by default. Voice notes in this fork are not a hands-free conversation mode.
- **Next: API-server connector:** separate credential store and protocol adapter for `API_SERVER_KEY` bearer auth and `/v1` endpoints, capability-detected UI, and tests against stock Hermes. Do not point current dashboard forms at an API-server URL.
- **Later: device tools:** require per-capability opt-in, visible active-use indicators, bounded scope, revocation, and a separate trust assessment of any relay plugin. Do not silently inherit chat permissions as device permission.

## Protocol/security boundary

Official docs: [dashboard](https://hermes-agent.nousresearch.com/docs/user-guide/features/web-dashboard), [API server](https://hermes-agent.nousresearch.com/docs/user-guide/features/api-server), and [programmatic integration](https://hermes-agent.nousresearch.com/docs/developer-guide/programmatic-integration).

- Dashboard: REST and JSON-RPC `/api/ws`, normally port 9119; non-loopback binding requires password or OAuth/OIDC. Remote WebSocket chat needs an authenticated session and a single-use ticket. A status-page probe alone does not prove chat works.
- API server: OpenAI-compatible HTTP/SSE, normally port 8642, `API_SERVER_KEY` bearer auth even on loopback. It grants access to agent tools. Dashboard credentials and API keys are different secrets with different lifecycles.
- Prefer private VPN or authenticated HTTPS for remote access. Plain HTTP/WS does **not** encrypt credentials; do not expose an unprotected dashboard or API server to the Internet. Cleartext transport remains enabled in the upstream Android app for trusted LAN compatibility; users must choose their network accordingly.

## Validation boundary

This fork retains the original app code, license, package ID and signing setup. Do not install it over the upstream app expecting independent identity. Source review and CI may verify compilation and tests, but live-provider, physical-device and release-signing verification remain separate gates. Fork changes should use feature branches and PRs against `dev`.
