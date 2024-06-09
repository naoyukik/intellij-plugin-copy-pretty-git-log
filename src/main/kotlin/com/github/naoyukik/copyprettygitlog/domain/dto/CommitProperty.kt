package com.github.naoyukik.copyprettygitlog.domain.dto

enum class CommitProperty {
    AUTHOR_NAME,
    COMMITER_NAME,
    COMMIT_TIME,
    FULL_MESSAGE,
    SUBJECT;

    val placeholder: String
        get() = "{$name}"
}
