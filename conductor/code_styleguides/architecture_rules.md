# Architecture Rules

本書は Copy Pretty Git Log のアーキテクチャ規則を規定する。

**本書の由来**: 旧 `kotlin-custom-word-separators` スキルの `references/architecture.md` は
別リポジトリ（`customize-word-separators-kt`、パッケージ `net.dstribe.customize_word_separators`）
から複製されたものであり、本プロジェクトの実態を記述していない。`AGENTS.md` が同ファイルを
「最高位の設計指針」と宣言していたが、本書が置き換えとしての正品である。

## 1. 層の定義

本プロジェクトは 3 層構造をとる。Application 層は存在しない。

| 層 | パッケージ | 責務 | IntelliJ API への依存 |
|---|---|---|---|
| Presentation | `presentation` | `AnAction` 実装。イベント受付と整形・クリップボード転送 | 許可 |
| Settings | `settings` | `PersistentStateComponent` による設定保持と設定 UI | 許可 |
| Domain | `domain` | プラットフォーム非依存の値と型 | **禁止** |

### 1.1 依存方向

矢印は常に外側から内側へ向ける。

```
presentation ──> domain
     │
     └────────> settings
```

- `presentation` から `domain` への依存: **許可**
- `presentation` から `settings` への依存: **許可**
- `domain` から `settings` への依存: **禁止**
- `domain` から `presentation` への依存: **禁止**
- `settings` から `presentation` への依存: **禁止**

## 2. Domain 層の規則

- IntelliJ API を import してはならない。`AnActionEvent`、`AnAction`、
  `VcsCommitMetadata`、`CopyPasteManager` などが該当する。
- 設定クラス（`AppSettingsState`）を参照してはならない。必要なら
  Domain 層にインターフェースを定義し、外側の層に実装させる。
- 整形ロジックはここに置く。Presentation 層にドメインロジックを書かない。

### 2.1 現状の違反

`domain/SettingState.kt` は以下の 2 点を同時に 위반している。

```kotlin
package com.github.naoyukik.copyprettygitlog.domain

import com.github.naoyukik.copyprettygitlog.settings.AppSettingsState  // 2-a
import com.intellij.openapi.actionSystem.AnActionEvent                 // 2-b

class SettingState {
    fun getAppSettingsState(e: AnActionEvent): AppSettingsState? {
        return e.project?.getService(AppSettingsState::class.java)
    }
}
```

- **2-a**: `domain` から `settings` への依存（禁止事項）
- **2-b**: `domain` からの IntelliJ API 依存（禁止事項）

このクラスはドメインロジックを持たない。`AnActionEvent` から
プロジェクトサービスを引く薄いブリッジにすぎない。Domain 層に置く理由がない。

**是正方針**: Presentation 層へ移動するか、Domain 層の抽象
（`SettingsProvider` 等のインターフェース）に置き換える。いずれを採っても
`domain` パッケージは IntelliJ API 非依存となり、ユニットテストが可能になる。

## 3. Presentation 層の規則

- `AnAction` の実装のみを置く。
- 整形処理の実装は Domain 層へ委譲する。`actionPerformed` が
  文字列生成のロジックを直接持つな。
- IntelliJ のスレッド制約を守る。詳細は 4 節を参照。

## 4. スレッド制約

`actionPerformed` は EDT（Event Dispatch Thread）上で呼ばれる。
重量処理をそのまま実行すると UI が凍結する。

現在の `CopyPrettyGitLogAction` は以下の 2 段構えになっている。

```
actionPerformed (EDT)
  └─ executeOnPooledThread        <- vcsLog.cachedMetadata の取得、整形
       └─ invokeLater            <- CopyPasteManager への書き込み
```

**規則**:

- クリップボード操作（`CopyPasteManager`）は EDT 上で行う。
  pooled thread からは `invokeLater` で切り替えること。
- 整形処理は pooled thread で行う。整形処理中に
  `state.myState` を読むため、設定が変更されていると
  一貫性が崩れる。スナップショットを EDT 上で採取してから
  pooled thread へ渡すこと。

## 5. IntelliJ API の使用

### 5.1 内部 API の禁止

プラットフォームの公開 API のみを使用する。特に
`com.intellij.util.indexing.diagnostic` 配下は内部パッケージであり、
公開 API ではない。

現在の `CopyPrettyGitLogAction` は以下を import している。

```kotlin
import com.intellij.util.indexing.diagnostic.TimeMillis
```

現状 `TimeMillis` は `Long` への typealias であるため実行時依存は生じないが、
公開 API ではないという事実が残る。プラットフォーム側が
この型定義を変更した場合に発生するリスクがある。

**対応方針**: IntelliJ Platform 2026.3 対応時に、
`VcsCommitMetadata.commitTime` の実際の型（`Long`）を直接使う
選択肢を評価する。

### 5.2 使用中のプラットフォーム API

| API | 用途 | リスク |
|---|---|---|
| `VcsLogDataKeys.VCS_LOG_COMMIT_SELECTION` | 選択コミットの取得 | 低。Git4Idea の VCS Log の公開契約 |
| `VcsCommitMetadata` | コミット情報の取得 | 中。フィールドの追加・変更あり得る |
| `CopyPasteManager` | クリップボード書き込み | 低。長期安定 |
| `com.intellij.ui.dsl.builder` | 設定 UI | 低。Kotlin UI DSL 2.0 の標準 API |
| `PersistentStateComponent` | 設定永続化 | 低。長期安定 |
| `com.intellij.util.indexing.diagnostic.TimeMillis` | 時刻型 | **高**。内部 API（5.1 参照） |

## 6. 命名規則

| 対象 | 形式 | 本プロジェクトの実例 |
|---|---|---|
| クラス・インターフェース・オブジェクト | `PascalCase` | `CommitProperty`, `AppSettingsState` |
| 関数・変数・プロパティ | `camelCase` | `reversedList`, `customPattern1` |
| 列挙体の定数 | `SCREAMING_SNAKE_CASE` | `AUTHOR_NAME`, `COMMITER_NAME` |
| パッケージ | `lower.case` | `com.github.naoyukik.copyprettygitlog` |
| Boolean 変数・関数 | 述語形式（`is`, `has`, `can`） | `isReversed` |

**IntelliJ Platform 固有**:

- Action: `<ActionName>Action` → `CopyPrettyGitLogAction`
- Service: `<ServiceName>Service`
- Configurable: `<SettingsName>Configurable` → `AppSettingsConfigurable`
- State: `<Feature>State` → `AppSettingsState`

**鉄則**:

- `tmp`, `data` などの曖昧な名前を避ける。
- 略語を避ける（`cnt` → `count`、`idx` → `index`）。

## 7. 物理的制約

| 項目 | 値 | 根拠 |
|---|---|---|
| 1 行の文字数 | 120 | `.editorconfig` の `max_line_length` |
| インデント | スペース 4 | `.editorconfig` の `indent_size` |
| 行末 | LF | `.editorconfig` の `end_of_line` |
| 末尾改行 | 必須 | `.editorconfig` の `insert_final_newline` |
| YAML のインデント | スペース 2 | `.editorconfig` の `[*.{yaml,yml}]` |
| 静的解析の閾値 | 0 件 | `config/detekt/detekt.yml` の `maxIssues: 0` |
| 自動整形 | 有効 | `build.gradle.kts` の `autoCorrect = true` |
