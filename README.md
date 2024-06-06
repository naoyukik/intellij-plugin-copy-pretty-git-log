# intellij-plugin-copy-pretty-git-log

![Build](https://github.com/naoyukik/intellij-plugin-copy-pretty-git-log/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/com.github.naoyukik.copyprettygitlog)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/com.github.naoyukik.copyprettygitlog.svg)](https://plugins.jetbrains.com/plugin/com.github.naoyukik.copyprettygitlog)

<!-- Plugin description -->
This plugin allows you to easily copy Git commit histories in a formatted text to your clipboard.
<!-- Plugin description end -->

## Installation

1. Open IntelliJ IDEA.
2. Open the 'File' menu, then select 'Settings'.
3. From the left-side menu, select 'Plugins'.
4. In the 'Marketplace' tab, search for 'Copy Pretty Git Log'.
5. Click 'Install' to start the plugin installation.
6. Once installation is complete, restart IDEA.

## Usage

1. Select commits in your VCS log.
2. Right-click and select 'Copy Pretty Git Log'.
3. The formatted log is copied to your clipboard.

## Configuration

- **Custom Pattern**: Allows you to define the format of your log strings. You can use placeholders such as `{AUTHOR_NAME}`, `{COMMITER_NAME}`, `{COMMIT_TIME}`, `{FULL_MESSAGE}`, and `{SUBJECT}`.
- **Reverse**: Enabling this will output your log entries in reverse order.
- **Custom Time Format**: Allows setting a custom date and time format. Default is `yyyy-MM-dd HH:mm:ss`.

## 概要
このプラグインは、Gitのコミット履歴を選択し、フォーマットしたテキストとしてクリップボードにコピーするのを簡単にします。

## インストール

1. IntelliJ IDEAを開きます。
2. 「File」メニューを開き、その中から「Settings」を選択します。
3. 左側のメニューから「Plugins」を選択します。
4. 「Marketplace」タブを開き、「Copy Pretty Git Log」を検索します。
5. 「Install」をクリックし、プラグインのインストールを開始します。
6. インストールが完了したら、IDEAを再起動します。

## 使い方

1. VCSログのコミットを選択します。
2. 右クリックメニューから、「Copy Pretty Git Log」を選択します。
3. フォーマットされたログがクリップボードにコピーされます。

## 設定

- **カスタムパターン**: ログ文字列のフォーマットを定義できます。 `{AUTHOR_NAME}`、 `{COMMITER_NAME}`、 `{COMMIT_TIME}`、 `{FULL_MESSAGE}`、 および `{SUBJECT}` などのプレースホルダーを利用できます。
- **リバース**: この設定を有効にすると、ログエントリは逆順で出力されます。
- **カスタム時間フォーマット**: カスタム日時フォーマットを設定できます。デフォルトは `yyyy-MM-dd HH:mm:ss` です。

## Installation

- Using the IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "intellij-plugin-copy-pretty-git-log"</kbd> >
  <kbd>Install</kbd>
  
- Manually:

  Download the [latest release](https://github.com/naoyukik/intellij-plugin-copy-pretty-git-log/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
