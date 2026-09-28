# Project Workflow

本書は Copy Pretty Git Log の開発手順を規定する。**すべての開発作業はこの文書に従う。**

本書は Conductor の既定テンプレートを、本プロジェクトの実績に即して書き直したものである。
Web 開発・モバイル・リレーショナルデータベースを前提とする記述は
適用可能性の検証を経て削除した。

## 1. 基本原則

1. **plan.md が唯一の実行計画**: すべての作業は `plan.md` に記録してから行う
2. **Tech Stack は明示的に変更する**: スタックを変更する場合は
   `tech-stack.md` を**実装前に**更新する
3. **TDD は変更種別で適用する**: 4 節の判定表に従う
4. **完了は物証で証明する**: 推論や「はずだ」で完了を宣言しない（8 節）
5. **フェーズは原子的**: フェーズをまたぐ変更を 1 つのコミットに混在させない
6. **手動検証は自己完結させない**: ユーザーの明示承認を得る（7 節）

## 2. タスクのライフサイクル

```
[ ]  未着手  ->  [~]  実施中  ->  [x]  完了（コミットハッシュ付き）
```

### Step 1: タスク選択

`plan.md` を読み、順序的に次の未着手タスク `[ ]` を選ぶ。

### Step 2: 実施中へマーク

`plan.md` の該当行を `[ ]` から `[~]` に変更する。

### Step 3: 変更種別の判定

4 節の判定表を参照し、Red フェーズが必要かを確定する。
この判定を省略してはならない。

### Step 4: 実施

- **Red フェーズが必要な場合**: 期待動作を定義するテストを書き、
  実行して期待どおり失敗することを確認する。確認前に実装へ進まない
- **Red フェーズが免除される場合**: 検証方法と証跡の入手手順を計画する（4 節）

### Step 5: 検証

- テスト: `./gradlew test`
- 静的解析: `./gradlew detekt`
- 品質ゲート: `./gradlew check`
- プラットフォーム検証: 変更が `plugin.xml` へ影響する場合のみ `./gradlew verifyPlugin`

実行前に、実行するコマンドをユーザーに明示する。

失敗した場合は**最大 2 回まで**修正を提案できる。
2 回試行しても解決しない場合は作業を停止し、原因と状況を報告して
ユーザーに指示を求める。**根を隠して先に進めるな。**

### Step 6: コミット

- 変更したファイルは**個別に** `git add <file>` でステージする
- `git add .` と `git add -A` は**禁止**する
  （`.gemini/hooks/restrict_git_add_all.py` が拒否する）
- `conductor/` 配下の文書のみ `git add conductor/` を許可する
- コミットメッセージは 6 節の規約に従う

### Step 7: plan.md の更新

- 該当タスクを `[~]` から `[x]` に変更する
- 同じ行にコミットハッシュの先頭 7 文字を追記する

```markdown
- [x] Task: 内容をここに書く [a1b2c3d]
```

- 実施内容と検証結果を、タスクの下に追加subsection として記録する

### Step 8: plan.md 更新のコミット

更新した `plan.md` を `git add conductor/` でステージし、
`conductor(plan): タスク '<タスク名>' を完了として記録` でコミットする。

## 3. スタック偏差の手順

実装が `tech-stack.md` と異なる場合は以下を実施する。

1. **実装を停止する**
2. `tech-stack.md` を新しい設計に更新する
3. 偏差の理由を日付入りで記録する
4. **ユーザーの承認を得る**
5. 実装を再開する

## 4. TDD ゲート（変更種別による判定）

本プロジェクトは 2026-09 時点でユニットテストが 0 件である。
無条件に Red フェーズを要求すると、ビルド設定の変更だけで終わるフェーズが
永久に完了しない。よって変更種別でゲートを分ける。

| 変更種別 | Red フェーズ | 完了の証跡 |
|---|---|---|
| **ドメインロジック**（`domain` 配下の計算・整形処理） | **必須** | 失敗するテストの出力、その後成功するテストの出力 |
| **Presentation / Settings のロジック** | **必須** | 同上 |
| **ビルド設定**（`build.gradle.kts`、`gradle.properties`、`libs.versions.toml`） | 免除 | ビルド成功ログと、生成物（`plugin.xml` 等）の実測値 |
| **プラグイン宣言**（`plugin.xml`、リソースバンドル） | 免除 | `buildPlugin` 成功ログと zip 内の実ファイル |
| **CI 設定**（`.github/workflows/*`） | 免除 | 構文検証結果と実際の run のログ |
| **文書**（`README.md`、`CHANGELOG.md`、`conductor/*`） | 免除 | 差分の自己レビュー |
| **静的解析設定**（`config/detekt/*`） | 免除 | `detekt` の実行結果 |

### 4.1 Red フェーズ免除時の義務

免除されたタスクでは、**代わりに実行結果を物証として提示しなければならない**。

- 変更前の実測値を記録する（例: `plugin.xml` の `until-build="262.*"`）
- 変更後に同値を再計測する（例: `until-build` 属性が消えている）
- 差分を提示する

「削除した」と書くだけでは証跡にならない。削除後のファイル内容を提示せよ。

## 5. フェーズ完了時の必須メタタスク

**すべてのフェーズの末尾に、以下の 4 タスクが存在することを `plan.md` で確認すること。**
存在しなければフェーズは完了としない。

```markdown
- [ ] Task: Conductor - Static Analysis (Detekt) & Format Check
- [ ] Task: Conductor - `gradle check` を実行して品質を検証
- [ ] Task: Conductor - User Manual Verification '<Phase Name>'
- [ ] Task: Conductor - '<Phase Name>' の成果をコミット
```

**注意**: `&&` は使えない。上記 4 タスクは**個別に**実行する。

### 5.1 Static Analysis の実行

```bash
./gradlew detekt
```

`config/detekt/detekt.yml` は `maxIssues: 0` であり、指摘が 1 件でもあれば失敗する。
`autoCorrect = true` のため、整形に関する指摘は自動修正される。

### 5.2 品質ゲート

```bash
./gradlew check
```

### 5.3 ユーザー手動検証

7 節を参照。**自己完結させてはならない。**

### 5.4 フェーズの成果のコミット

- フェーズ内の変更のみをステージする
- 他フェーズの変更・無関係な変更を巻き込めない
- メッセージは 6 節に従う

## 6. コミット規約

`AGENTS.md` が定める規約が正本である。ここでは実行上の要点のみ示す。

### 6.1 フォーマット

```text
<type>: 日本語での説明（50文字以内）

- 変更の理由、または達成目的
- 箇条書きで記載

ref: <IssueNumber>
```

- `ref:` の IssueNumber はブランチ名の `^[0-9]+-` にマッチする数字
- タイトル・本文は**日本語**。英語表記は不可
- 1 行目は 50 文字以内
- 説明は「何をしたか」ではなく「なぜ必要か」を書く
- **署名のトレイラーは付与しない。** メッセージ末尾に
  `Co-Authored-By:` 等の署名トレイラーを追加してはならない

### 6.2 type

`feat` / `fix` / `docs` / `style` / `refactor` / `test` / `chore`

### 6.3 ステージングの禁止事項

```bash
git add .          # 禁止
git add -A         # 禁止
git add --all      # 禁止
```

変更したファイルは個別に指定する。

```bash
git add gradle.properties
git add build.gradle.kts
git add CHANGELOG.md
```

例外: `conductor/` 配下の文書は一括ステージを許可する。

```bash
git add conductor/
```

## 7. ユーザー手動検証プロトコル

**この検証は、いかなる理由があっても自己完結させてはならない。**

1. フェーズ完了をユーザーに宣言する
2. 完了したフェーズで変更されたファイルを特定する
   ```bash
   git diff --name-only <前のフェーズのチェックポイント SHA> HEAD
   ```
3. 自動テストを実行する（5.1、5.2 のコマンド）
4. `product.md`、`product-guidelines.md`、`plan.md` を読み、
   フェーズが目的とするユーザー可視の目標を特定する
5. ユーザーが実行すべき手順を番号付きで提示する

   **JetBrains プラグインの場合の記載例**:

   ```text
   自動テストは通過しました。手動検証を次の手順で行ってください。

   1. 次のコマンドで IntelliJ IDEA を起動してください:
      ./gradlew runIde
   2. 任意の Git リポジトリで VCS Log を開いてください
   3. コミットを 1 つ以上選択し、右クリックしてください
   4. 「Copy Pretty Git Log」が表示されたら選択してください
   5. 次のことを確認してください:
      - クリップボードに整形済みのテキストが 1 行ずつ入っている
      - 日付の書式が「設定 > Tools > Copy Pretty Git Log」で
        設定した形式になっている
   ```
6. **ユーザーの回答を待つ。** 明示的な承認を得ずに次へ進まない
7. 承認取得後、チェックポイントコミットを作成する
8. 検証レポート（実行コマンド・手順・ユーザーの承認内容）を
   `plan.md` のフェーズ完了記録に追記する

## 8. 完了の定義

**完了と報告する前に、以下を物理的に確認する。**

- [ ] `git status` で意図しない変更が混入していない
- [ ] 実行したコマンドの**実出力**を取得した
- [ ] 「完了した」と述べるのに、推論ではなく証拠に基づいている
- [ ] 検証できなかった項目があれば、その理由と未確認範囲を明記した

### 8.1 禁止される報告

以下は**完了の証拠にならない**。禁止する。

- 「XxxCode にこう書いたため、動作するだろう」
- 「テストが通ったはず」
- 「ビルドは通ると思う」
- 実行していないコマンドの結果を推測で記載する

実出力を得られなかった場合は、以下のように正直に報告する。

```text
検証未実施: <理由>
未確認範囲: <具体的に何を確認できていないか>
```

## 9. 開発コマンド

### セットアップ

```bash
# Foojay resolver が JDK 21 を自動取得する
./gradlew --version
```

### 日常開発

```bash
./gradlew test              # ユニットテスト
./gradlew detekt            # 静的解析・自動整形
./gradlew check             # 品質ゲート（Detekt + test + Kover）
./gradlew runIde            # IntelliJ IDEA を起動して手動確認
```

### プラグインのビルドと検証

```bash
./gradlew buildPlugin               # 配布用 zip を生成
./gradlew verifyPlugin              # プラグイン構成の検証
./gradlew runPluginVerifier         # IntelliJ でのバイナリ互換性の検証
./gradlew verifyPluginProjectConfiguration   # プロジェクト構成の検証
```

生成物の確認:

```bash
# 生成された plugin.xml が意図した内容かを確認する
unzip -p build/distributions/*.zip "*/META-INF/plugin.xml"
```

### コミット前

```bash
./gradlew detekt
./gradlew check
```

**注意**: `.run/Run Verifications.run.xml` が参照する `runPluginVerifier` は
IntelliJ Platform Gradle Plugin 1.x のタスク名である。正しいタスクは
`verifyPlugin` である。実行構成は本書の作成時点で更新されていない。

## 10. テスト要件

### 現状の制約

`src/test` は存在せず、ユニットテストは 0 件である。`build.gradle.kts` は
`testFramework(TestFrameworkType.Platform)` と `useJUnitPlatform()` を
設定しているが、実行対象のテストクラスが無い。

`build.yml` は Kover レポートを Codecov へ送信するが、
測定対象が無いため意味を持たない。`build.yml` は Kotest のタグ除外引数
`-Dkotest.tags.exclude=Learn` を渡すが、`libs.versions.toml` に
Kotest の依存定義が無い。

### 新規追加するテストの要件

- テストの追加は `src/test/kotlin/` 配下とする
- パッケージ構成は `src/main/kotlin` のミラーとする
- 命名は `<対象クラス名>Test` とする
- Domain 層のテストは IntelliJ Platform を起動しない純粋な JVM テストとする
  （`architecture_rules.md` 2 節のとおり、Domain 層は API 非依存である）

### カバレッジ

**数値閾値は設けない。** 現状 0 件であり、数値ゲートを設けた時点で
すべてのフェーズが恒久的に失敗する。

代わりに、**新規に Domain 層のロジックを追加するタスクでは、
そのロジックに対するテストが存在することを Quality Gate とする。**

## 11. コードレビュー

### 着自己レビュー

レビューを依頼する前に、以下を自分で確認する。

1. **機能**: 仕様どおりに動作するか。境界値はどう扱うか
2. **品質**: `code_styleguides/architecture_rules.md` の規則に従っているか。
   依存方向は正しいか。命名規則は合っているか
3. **テスト**: 4 節の判定表に従い、Red フェーズが必要だったか。
   必要だった場合にテストがあるか
4. **スレッド**: EDT 上で重い処理を実行していないか（`architecture_rules.md` 4 節）
5. **API**: 内部 API を使用していないか（`architecture_rules.md` 5.1）
6. **機密情報**: 認証情報・秘密鍵・`sign*.zip` をコミットしていないか
7. **範囲外の変更**: 依頼されていない変更が混入していないか

### 除外される観点

本プロジェクトに存在しない下列の観点は、レビューに含めない。

- モバイル・タッチ操作・レスポンシブ対応
- SQL インジェクション・XSS（データベースもブラウザも存在しない）
- データベーストランザクション・マイグレーション
- パフォーマンスのマイクロ最適化（5 ファイルの規模では意味を持たない）

## 12. 非常時の手順

### 本番公開後に重大な不具合を検出した場合

1. `main` から修正ブランチを作成する
2. 修正のロジックに対して Red フェーズを実施する
   （4 節の判定表に従う。設定のみの変更なら免除）
3. 最小限の修正を実装する
4. `./gradlew check` と `./gradlew verifyPlugin` を実行する
5. 手動検証プロトコル（7 節）を実施する
6. `CHANGELOG.md` の `## [Unreleased]` に `### Fixed` として追記する
7. 修正をコミットし、PR を作成する

### 秘密情報がコミットされたと判明した場合

1. すべての書き込み操作を停止する
2. **該当のファイルの履歴を書き換える**（`git filter-repo` などを用いる）
3. 発行済みシークレットをすべて無効化する（発行済みトークンのローテーションを含む）
4. 影響範囲と実施した対策を `plan.md` に記録する
5. ユーザーに報告する

`sign*.zip`、`private.pem`、`chain.crt`、`.env` はリポジトリにコミットしてはならない。

## 13. デプロイ

### 公開前チェックリスト

- [ ] `./gradlew detekt` が成功する
- [ ] `./gradlew check` が成功する
- [ ] `./gradlew verifyPlugin` が成功する
- [ ] `./gradlew runPluginVerifier` が成功する
- [ ] 手動検証プロトコル（7 節）でユーザーの承認を得ている
- [ ] `CHANGELOG.md` が更新されている
- [ ] `pluginVersion` がインクリメントされている
- [ ] `README.md` の手順が実際の挙動と一致している
- [ ] 公開物の署名に必要な環境変数が設定されている

### 公開手順

1. バージョンリリースブランチを `main` へマージする
2. `./gradlew patchChangelog` で `CHANGELOG.md` を確定させる
3. `./gradlew publishPlugin` で Marketplace へ公開する
   （認証情報は環境変数 `PUBLISH_TOKEN`）
4. Marketplace 上の公開内容と `until-build` の状態を確認する
5. リリース Issue を作成する

### 公開後

1. Marketplace の公開状態を確認する
2. 利用者の Issue を監視する
3. 次のトラックの候補を記録する
