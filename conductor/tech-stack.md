# Tech Stack

本書は Copy Pretty Git Log の技術スタックを規定する。
現在値の記録と、スタック変更時の判断基準の両方を目的とする。

## 1. 言語とランタイム

| 項目 | 値 | 備考 |
|---|---|---|
| 実装言語 | Kotlin 2.3.21 | Java ソースは存在しない |
| JVM ツールチェーン | 21 | `jvmToolchain(21)`。`settings.gradle.kts` の Foojay resolver 1.0.0 が取得する |
| ソース数 | 5 ファイル | 全て `src/main/kotlin` 配下 |

`kotlin("plugin.serialization")` 2.3.21 を適用しているが、現時点で
`kotlinx.serialization` を使ったコードは存在しない。削除の是非は
別途検討する（本トラックのスコープ外）。

## 2. IntelliJ Platform

| 項目 | 値 |
|---|---|
| Gradle Plugin | `org.jetbrains.intellij.platform` 2.16.0（2.x 系） |
| ターゲット IDE | `IU`（IntelliJ IDEA Ultimate） |
| プラットフォームバージョン | `262.6653.22`（IntelliJ IDEA 2026.2） |
| `sinceBuild` | `243`（2024.3） |
| `untilBuild` | `262.*` |
| 同梱プラグイン依存 | `Git4Idea` |
| 設定永続化 | `PersistentStateComponent`（サービスレベル `PROJECT`） |
| UI 構築 | Kotlin UI DSL（`com.intellij.ui.dsl.builder`） |

### 2.1 untilBuild に関する方針

JetBrains は 2024.3 以降のプラグインについて `until-build` の設定を
推奨していない。`untilBuild` の指定は、JetBrains 公式の告知でも
削除が推奨されている。

**方針**: 削除する。削除時の選択肢は次の 2 つである。

- `gradle.properties` の `pluginUntilBuild` を削除し、
  `build.gradle.kts` の `ideaVersion.untilBuild` 代入自体を消す
- `ideaVersion.untilBuild = provider { null }` と明示する

いずれでも `plugin.xml` に `until-build` 属性が出力されなくなることを確認する。

## 3. ビルド

| 項目 | 値 |
|---|---|
| Gradle | 9.2.1（wrapper） |
| Configuration Cache | 有効 |
| Build Cache | 有効 |
| Foojay toolchain resolver | 1.0.0 |
| 署名 | `zipSigner` 有効。認証情報は環境変数（`CERTIFICATE_CHAIN` / `PRIVATE_KEY` / `PRIVATE_KEY_PASSWORD`） |
| 検証 | `pluginVerifier` 有効。`pluginVerification.ides` は `recommended()` |
| Kotlin ランタイム | バンドルしない（`kotlin.stdlib.default.dependency = false`） |

## 4. 品質保証

| ツール | バージョン | 設定 |
|---|---|---|
| Detekt | 1.23.8 | `maxIssues: 0`、`autoCorrect = true`、`buildUponDefaultConfig = true`、設定は `config/detekt/detekt.yml`（1053 行） |
| detekt-formatting | 1.23.8 | `detektPlugins` 経由で適用 |
| Kover | 0.9.1 | XML レポートを `onCheck = true` で生成 |
| Qodana | 2025.1.1 | `qodana.yml` |
| Gradle Changelog | 2.4.0 | `CHANGELOG.md` を `## [Unreleased]` 形式で解析 |

**現状の限界**: `src/test` が存在せず、ユニットテストは 0 件。
`tasks.withType<Test>` は `useJUnitPlatform()` を設定するが、
実行対象のテストクラスが無い。`build.yml` は Kotest のタグ除外引数
（`-Dkotest.tags.exclude=Learn`）を渡すが、`libs.versions.toml` に
Kotest 依存の定義がない。

## 5. CI

| ワークフロー | トリガ | 内容 |
|---|---|---|
| `build.yml` | `main` への push、全 pull_request | wrapper 検証、`check`、`verifyPlugin`、Qodana、`buildPlugin`、`runPluginVerifier`、下書き Release 作成 |
| `release.yml` | 手動 | リリース発行 |
| `run-ui-tests.yml` | 手動（`workflow_dispatch`） | `runIdeForUiTests` + robot server による UI テスト |

- `build.yml` と `run-ui-tests.yml` は Java 17 で Gradle を実行する。
  JDK 21 は Foojay resolver が取得する。
- Kover の XML レポートを Codecov へアップロードする。
- 両ワークフローは IntelliJ Plugin Template 由来であり、
  1.x 系 IntelliJ Platform Gradle Plugin を前提とした記述
  （例: `gradle runIdeForUiTests` における素の `gradle` 呼び出し）が
  残っている。実際のタスク定義は `build.gradle.kts` の
  `intellijPlatformTesting.runIde.register("runIdeForUiTests")` である。
- `.run/Run Verifications.run.xml` は `runPluginVerifier` を参照するが、
  これは 1.x のタスク名である。2.x では `verifyPlugin` が正しい。

## 6. ソースコード構成

```
src/main/kotlin/com/github/naoyukik/copyprettygitlog/
├── domain/
│   ├── SettingState.kt          # AnActionEvent から設定を取得する橋渡し
│   └── dto/
│       └── CommitProperty.kt    # プレースホルダ列挙体（5 種）
├── presentation/
│   └── CopyPrettyGitLogAction.kt # AnAction 実装。中核ロジック
└── settings/
    ├── AppSettingsState.kt      # PersistentStateComponent
    └── AppSettingsConfigurable.kt # 設定 UI（Kotlin UI DSL）
```

`src/main/resources/META-INF/plugin.xml` が唯一のプラグイン記述であり、
`Git4Idea` への依存と `Git.Log.ContextMenu` へのアクション登録を宣言する。

### 6.1 依存方向の現状と課題

`domain/SettingState.kt` が `settings/AppSettingsState` を import しており、
依存の矢印が `domain` から `settings` へ向いている。`AGENTS.md` が定める
依存性逆転の原則とは逆である。本トラックのスコープ外だが、
`product.md` の Known Gaps として記録済みである。
