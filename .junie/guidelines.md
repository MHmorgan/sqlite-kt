# AI Assistant Guidelines

Explain your actions and decisions clearly and always provide the reasoning behind your choices.


## Code Style

Prefer short, concise one-word names and use common abbreviations.

Use longer descriptive names if it is required for clarity.
If a variable is used far from its declaration, a descriptive name should be used.

Avoid complicated function calls (e.g., `foo(bar(baz()))`), use intermediate variables to clarify intent.

All methods should be documented with KDoc comments, explaining their purpose, usage and pitfalls (if any).


## Dependencies

Avoid adding third-party dependencies for solving tasks, unless told to.

If adding a dependency is necessary, always choose stable and mature libraries.

