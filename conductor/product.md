# Copy Pretty Git Log

## Overview

IntelliJ IDEA 向け JetBrains プラグイン。Git の VCS Log で選択したコミットを、
利用者が定義したパターンで整形し、クリップボードへコピーする。

プラグイン ID は `com.github.naoyukik.copyprettygitlog`。単一アクション・単一コンテキストメニューという
小さな表面積に閉じたユーティリティであり、バックエンド・永続化・ネットワーク通信を持たない。

## Users

IntelliJ IDEA 上で Git を使う開発者が、Issue・Pull Request・レビューコメント・
リリースノートに貼るコミット一覧の下書きを、整形済みのテキストとして手元に欲しい人物である。
Git ツールの GUI 自体ではなく、テキストとして出力された内容を目的とする。

## Value Proposition

- 既存の Git GUI では 얻られない情報を、利用者が定義した任意の形式へ再構成し、
  貼り付け可能な形にして得られる。
- プレースホルダによる書式指定により、用途ごとに異なる出力形式を 1 つのプラグインで賄える。

## Functional Scope

### コア機能

1. **コミットの整形出力**
   - VCS Log 上で選択したコミット集合を、指定パターンに従って文字列化する。
   - パターン内で使用可能なプレースホルダは 5 種。
     `{AUTHOR_NAME}` / `{COMMITER_NAME}` / `{COMMIT_TIME}` / `{FULL_MESSAGE}` / `{SUBJECT}`
   - 出力をクリップボードへコピーする。

2. **プロジェクト単位の設定**
   - `AppSettingsState`（`PersistentStateComponent`）が以下 3 項目を保持する。
     - カスタムパターン（既定値 `- {SUBJECT} {COMMIT_TIME}`）
     - 逆順フラグ（既定値 `false`）
     - 時刻書式（既定値 `yyyy-MM-dd HH:mm:ss`）
   - 保存先は `CopyPrettyGitLogState.xml`、サービスレベルは `PROJECT`。
   - 設定 UI は Kotlin UI DSL（`com.intellij.ui.dsl.builder`）で構成する。

### 起動経路

- `Git.Log.ContextMenu` に登録した単一アクションからのみ起動する。他にトリガーは設けない。

## Non-Functional Requirements

- **互換性**: IntelliJ Platform `243` 以降（2024.3 以降）に対応する。
- **配布**: JetBrains Marketplace への公開を前提とする。
- **ビルド再現性**: Gradle Configuration Cache と Build Cache を有効とする。
- **静的解析**: Detekt を `maxIssues: 0` で CI に強制する。
- **Kotlin ランタイム**: IntelliJ プラットフォーム同梱版を利用し、プラグインにバンドルしない
  （`kotlin.stdlib.default.dependency = false`）。

## Known Gaps

以下は本 Product Definition の時点で検出した、既存コードと文書との不整合である。
いずれも Issue #173 の直接スコープ外だが、記録する。

| # | 内容 |
|---|---|
| 1 | `src/test` が存在せず、ユニットテストが 0 件。`build.gradle.kts` は Kover `onCheck = true` を有効化し、`build.yml` は Codecov へ送信するが、測定対象が存在しない |
| 2 | CI フラグに Kotest のタグ除外（`-Dkotest.tags.exclude=Learn`）があるが、`gradle/libs.versions.toml` に Kotest 依存がない |
| 3 | `domain/SettingState.kt` が `settings/AppSettingsState` を import しており、依存の矢印が domain から settings へ向いている。`AGENTS.md` の DIP 原則と矛盾する |
| 4 | `.run/Run Verifications.run.xml` が IntelliJ Platform Gradle Plugin 1.x のタスク名 `runPluginVerifier` を参照している。2.x における正しいタスク名は `verifyPlugin` |
| 5 | `jvmToolchain(21)` に対し、IntelliJ Platform 2026.2 以降は Java 25 を要求する |

## Out of Scope

- Git の VCS Log UI 自体の改良
- コミットの選択方法の変更（複数選択は既存挙動をそのまま利用する）
- クリップボード以外の出力先（ファイル書き出し、Issue 送信等）
- テンプレートファイルの管理機構
- 他プラグインの統合
- 上記 Known Gaps の修正（それぞれ別トラックで扱う）
