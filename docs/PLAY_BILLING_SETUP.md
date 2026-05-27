# Google Play Billing setup for Imio Premium

Use these IDs when you create products in Play Console **after** app review (or earlier on a draft subscription — products can exist before publish).

## Recommended IDs (already in the app)

| Field in Play Console | ID to enter | Notes |
|----------------------|-------------|--------|
| **Subscription product ID** | `imio_premium` | One subscription for all Premium access |
| **Base plan ID** (monthly) | `monthly` | Billing period: 1 month, auto-renewing |
| **Base plan ID** (yearly) | `yearly` | Billing period: 1 year, auto-renewing |

Defined in code: [`PremiumProductIds.kt`](../app/src/main/java/com/globaldevmax/app/imio/core/premium/PremiumProductIds.kt).

### Why these names

- `imio_premium` — clear, unique, matches app name; easy to find in Play Console and logs.
- `monthly` / `yearly` — short base plan IDs inside one subscription (Google Billing 5+ model). The app picks the plan by `basePlanId`, not by separate product IDs.

### Naming rules (Google)

- Lowercase letters, digits, underscores only.
- Must start with a letter.
- Cannot be changed after creation — choose once.

### Do not use (for this app)

- Separate subscription products like `imio_premium_monthly` and `imio_premium_yearly` — the app expects **one** subscription `imio_premium` with two **base plans**.
- IDs with hyphens (`imio-premium`) — not allowed.

---

## Play Console checklist (copy when ready)

1. [Google Play Console](https://play.google.com/console) → app **Imio** (`com.globaldevmax.app.imio`).
2. **Monetize → Products → Subscriptions** → **Create subscription**.
3. Subscription product ID: **`imio_premium`**
4. Add base plan:
   - ID: **`monthly`** → 1 month, auto-renewing, set price.
5. Add base plan:
   - ID: **`yearly`** → 1 year, auto-renewing, set price.
6. Activate both base plans.
7. **No free trial** (app is paid-only).
8. **Settings → License testing** — add tester Gmail accounts.
9. Upload **AAB** to **Internal testing** (`com.globaldevmax.app.imio`, not `.debug`).
10. Install from the test track and open **Premium** in the app.

---

## Testing

- Real billing works only with the Play-signed build from a test/production track.
- Debug package `com.globaldevmax.app.imio.debug` does **not** use the same Play products.
- **Restore purchases** after reinstall.
- Cancel in Google Play account → premium locks again after refresh.

---

## App behavior (no backend)

- Status: `queryPurchasesAsync` (subscriptions).
- Prices: `queryProductDetails` on Premium screen.
- Purchase: `launchBillingFlow` + `acknowledgePurchase`.
- Premium videos unlock via `isPremiumSubscriptionActive` on device only.
