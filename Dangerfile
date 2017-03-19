# dangerfile for danger configurations

# If these are all empty something has gone wrong, better to raise it in a comment
if git.modified_files.empty? && git.added_files.empty? && git.deleted_files.empty?
  fail "This PR has no changes at all, this is likely an issue during development."
end

if github.pr_body.contains? "Please describe"
  warn "Please update the PR template"
end
