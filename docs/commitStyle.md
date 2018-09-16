## Commit Style Guidelines for phimpme-android

### Message Structure
Commit messages consist of three distinct parts, separated by a blank line: the title, an optional body/content, and an optional footer/metadata. The layout looks like this:

type: subject

body

footer

***

### Title
The title consists of the subject and type of the commit message. 

### Type
The type is contained within the title and can be one of the following types:

* **feat:** a new feature
* **fix:** a bug fix
* **docs:** changes to documentation
* **style:** formatting, missing semi-colons, etc; no code change
* **refactor:** refactoring production code
* **test:** adding tests, refactoring test; no production code change
* **chore:** updating build tasks, package manager configs, etc; no production code change

### Subject
The subject is a single short line summarising the change. It should be no greater than 50 characters, should begin with a capital letter and do not end with a period.

Use an imperative tone to describe what a commit does, rather than what it did. For example, use fix; not fixed or fixes or fixing.

For example: 
- fix: Typo in Commit Style guidelines 
- feat: Update UI of SessionDetailsActivity
- fix: Remove deprecated methods
- refactor: API endpoints and JSON assets

Instead of writing the following: 
- Fixed bug with Y
- Changing behaviour of X


### Body
The body includes the kind of information commit message (if any) should contain. 

Not every commit requires both a subject and a body. Sometimes a single line is fine, especially when the change is self-explanatory and no further context is necessary, therefore it is optional. The body is used to explain the what and why of a commit, not the how.

When writing a body, the blank line between the title and the body is required and we should try to limit the length of each line to no more than 72 characters.

### The Footer
The footer is optional and is used to reference issue tracker IDs.

For example:
Fixes #1346

***
