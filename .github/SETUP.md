# Настройка репозитория

## 1. GitHub Pages

1. **Settings** → **Pages**
2. В **Build and deployment** → **Source** выбрать **GitHub Actions**
3. После мержа в `master` вкладка **Actions** покажет workflow, а сайт будет доступен по:
   ```
   https://<owner>.github.io/<repo>/
   ```

## 2. Защита ветки master

1. **Settings** → **Branches** → **Add rule**
2. **Branch name pattern**: `master`
3. Включить:
   - **Require a pull request before merging** (обязательный PR)
   - **Require approvals** — при необходимости указать число одобрений (например, 1)
   - **Require status checks to pass before merging** — выбрать чек из workflow **Deploy to GitHub Pages**
4. **Create**

### Через GitHub CLI (опционально)

```bash
gh api repos/OWNER/REPO/branches/master/protection \
  -X PUT \
  -H "Accept: application/vnd.github+json" \
  -f required_status_checks='null' \
  -f enforce_admins=false \
  -F required_pull_request_reviews='{"required_approving_review_count":1}' \
  -f restrictions='null'
```

Подставьте `OWNER` и `REPO` вместо placeholder'ов.

