# Auth & Config

## Auth model

Stateless token authentication via Spring Security. The `TokenAuthenticationFilter` reads a token from the `Authorization` header, validates it against Redis, and sets `SecurityContextHolder`. Controllers use `@PreAuthorize` with permission strings. URLs listed in `yudao.security.permit-all_urls` bypass authentication. Multi-tenant is enabled by default (`yudao.tenant.enable: true`).

## Key config properties (application.yaml)

- `yudao.info.base-package` = `cn.iocoder.yudao` — drives component scanning, MyBatis alias packages, codegen
- `yudao.tenant.enable` — toggles multi-tenant globally
- `yudao.security.mock-enable` — when true, allows mock login in dev (set in `application-local.yaml`)
- `yudao.demo` — demo mode, must be `false` for normal operation
- `yudao.captcha.enable` — toggle captcha (disabled in local profile)

## Lombok config

- `toString`/`equalsAndHashCode` call super by default
- Fluent accessor chaining is on (`@Accessors(chain = true)`)
