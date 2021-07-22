include("app", "repository")

buildCache {
    local {
        removeUnusedEntriesAfterDays = 1
    }
}