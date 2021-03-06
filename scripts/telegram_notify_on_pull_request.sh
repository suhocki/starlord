#!/usr/bin/env bash

if [[ "$TRAVIS_PULL_REQUEST" != "false" ]] ; then
BOT_URL="https://api.telegram.org/bot${telegram_bot_token}/sendMessage"

PARSE_MODE="Markdown"

send_msg () {
    curl -s -X POST ${BOT_URL} -d chat_id=@starlord_team \
        -d text="$1" -d parse_mode=${PARSE_MODE}
}

githubBotCommentsCount=$(curl -H "Authorization:token ${github_comment_bot_api_key}" -X GET "https://api.github.com/repos/${TRAVIS_REPO_SLUG}/issues/${TRAVIS_PULL_REQUEST}/comments" | jq ".. | objects | select(.user.id == ${GITHUB_BOT_ID})" | grep "body" | grep -c "Build successful.")

if [[ "$githubBotCommentsCount" -gt 0 ]]; then
    send_msg "
    @${AUTHOR_NAME} updated his [pull request](https://github.com/${TRAVIS_REPO_SLUG}/pull/${TRAVIS_PULL_REQUEST}).
    "

    else
    send_msg "
    Code-review time!
    Guys, take a moment to review [pull request](https://github.com/${TRAVIS_REPO_SLUG}/pull/${TRAVIS_PULL_REQUEST}) made by @${AUTHOR_NAME}, please.
    "
fi
fi
