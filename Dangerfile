# dangerfile for danger configurations

# env variables
pr_author = github.pr_author

# If these are all empty something has gone wrong, better to raise it in a comment
if git.modified_files.empty? && git.added_files.empty? && git.deleted_files.empty?
  fail "This PR has no changes at all, this is likely an issue during development."
end

if github.pr_body.include? "Please describe"
  warn "Please update the PR template"
end

message ":tada: Thanks for the PR @#{pr_author} we hope you have changed the variables in the upload_apk_forks.sh its for easy moderation."

# Message the user
markdown ":link: Maintainers: the apk to test can be found at https://github.com/#{pr_author}/phimpme-android/blob/apk/app-debug.apk"
