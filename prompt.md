---
name: selenium-test-generation
description: Analyse the provided story, analyse the current testing Selenium project and based on need execute the step on playwright MCP, and eventually generate Selenium Tests.
supporting_files:
  - supporting-files/generation-engine.md
  - supporting-files/evaluation-engine.md
  - supporting-files/locator-engine.md
  - supporting-files/reporting-engine.md
  - supporting-files/pmd-rules.md
  - scripts/watermark_enforcer.py
---

# ServiceNow Selenium Test Generation

## What I Do

Generate production-ready Java JUnit Selenium tests from ServiceNow user stories with:

- ✅ Automated code generation with Page Object pattern
- ✅ Iterative quality evaluation (targets 4/4 functional correctness and >= 8/10 overall score)
- ✅ Conditional Playwright MCP for missing locators
- ✅ Compilation, PMD, and runtime fixes
- ✅ Full state persistence for resumability
- ✅ Applies watermark to all generated or modified JavaDoc and inline comments.

## Quick Start

**For New Tests:**
  - User story identifier such as `STRY...` resolved through Now MCP
  - Test identifier such as `TEMT...` resolved through Now MCP
  - User story file path or attachment token such as `@userStory.txt`
  - Raw test steps and expected results pasted directly in chat

## Execution Contract

- Execute the workflow strictly in order: Step 0 → Step 1 → Step 2 → Step 3 → Step 4 → Step 5 → Step 6.
- Do not skip a step silently. The only conditional phase is Step 3, which must always record one explicit decision in `.workflow_state.json`: `execute`, `skip`, or `blocked_waiting_for_user`.
- Do not advance to the next step until the current step has written its required state keys and completion status.
- If required information is missing or ambiguous, ask the user, write the blocked reason to `.workflow_state.json`, and stop rather than guessing.
- If a delegated phase fails, persist the failure summary and continue only when the next phase is explicitly designed to consume failure state.
- The workflow is complete only after Step 5 has produced the user-facing accuracy summary and Step 6 has removed `.workflow_state.json`.

## Workflow

### Step 0: Input Normalization, Story Retrieval & Target Resolution

1. **Resolve Story**:
   - If the input is a direct file path, attachment token, or referenced workspace file, read that source directly before attempting any fallback search.
   - If the input matches a story or test identifier pattern such as `STRY...` or `TEMT...`, use Now MCP to fetch the corresponding authoritative story details before continuing.
   - Validate that the resolved story contains the necessary title, narrative, and test criteria.
   - If the input is raw narrative or steps, synthesize a normalized story object using chat context.
   - If an identifier or attachment token cannot be resolved confidently, stop and ask the user for the file path or story content instead of guessing.
   - Extract `story_slug`, `story_title`, `inputKind`, and `storySource` from the normalized story input.
2. **Resolve Code Targets**:
   - Define new `code_name` as `PascalCase(story_title) + "IT"`.
3. **Initialize State**: Save all resolved paths, story retrieval source, story validation outcome, naming decisions, and `workflowStatus.currentStep = "step0"` to `.workflow_state.json`.
4. **Completion Gate**: Step 0 is complete only when `story_slug`, `story_title`, `inputKind`, `storySource`, and the primary code target strategy are present in state.

### Step 1: Generate Selenium Java (JUnit)

[Delegates to @supporting-files/generation-engine.md]

- Consumes the already normalized and validated story details prepared in Step 0
- Discovers project structure (test/pageobject/utility directories)
- Generates Java JUnit test with Page Objects
- **CRITICAL**: Applies mandatory invisible watermark signature to all JavaDoc and inline comments.
- Applies the mandatory invisible-watermark comment policy to every affected generated Java file.
- Resolves the skill directory containing `scripts/watermark_enforcer.py` and runs the local module in `enforce` mode followed immediately by `validate` mode after every Java write performed by this skill.
- Confirms PO/Utils selection with user
- Saves directly to proper project locations
- Step 1 is complete only when all affected Java files are written, watermark `enforce` and `validate` both pass, `javaFilePath` plus any `pageObjectPaths` and `utilityPaths` are saved, and `workflowStatus.currentStep = "step1"` is recorded.

### Step 2: Iterative Evaluation and Refinement

[Delegates to @supporting-files/evaluation-engine.md]

- Evaluates functional correctness (target: 4/4) and overall score (target: >= 8/10)
- Regenerates with improvement plan if targets are not met
- **Never invents locators** - uses `// TODO` instead
- If the only remaining blocker is unresolved locator data, record that explicitly and hand off to Step 3 instead of looping indefinitely
- Step 2 is complete only when the latest evaluation summary, improvement plan, gate decision, and `workflowStatus.currentStep = "step2"` are saved

### Step 3: Determine Playwright MCP Requirement & Step 3a: Resolve Missing Locators via Playwright MCP

**Engine:** `locator-engine.md`

**Only runs if:**

- User story involves UI interactions (3+ UI keywords) AND
- Generated code has missing locators (`// TODO`, `REPLACE_ME`, etc.)

**Process:**

1. Collects Instance URL + User Role (checks state first)
2. **Interactively explores UI** via Playwright MCP (headed mode)
3. Extracts resilient locators during exploration (priority: data-testid → id → css → xpath)
4. Merges into Java code (merge-only, no refactoring)
5. Runs the local watermark enforcer (`scripts/watermark_enforcer.py`) in `enforce` mode and then `validate` mode on all affected Java files before continuing.
6. **No locator files generated** - pure direct merge.

**Key Principle:** Playwright MCP is used as an **exploration tool** to discover locators, not to generate a test suite.

**Required Outcomes:**

- Always write one explicit outcome to state: `execute`, `skip`, `completed_with_unmerged_locators`, or `blocked_waiting_for_user`.
- If Step 3 is skipped, record the rationale and continue to Step 4.
- If Step 3 needs user input, record the blocked reason and stop.
- If Step 3 updates Java code, watermark `enforce` and `validate` must pass before continuing.

**Outputs:**

- Updated Java code with merged locators

### Step 4: Validation, Compilation & Execution

**Delegate to:** `build-and-selfheal-selenium-tests`

- Do not perform PMD checks, compilation, or execution within this skill.
- Instead, use the `skill` tool to invoke `build-and-selfheal-selenium-tests`.
- **Crucially**, when handing off to the skill, explicitly provide it with the target Java file path (retrieved from `javaFilePath` in `.workflow_state.json`).
- That delegated skill will handle the unified loop of PMD static analysis, Maven compilation, test execution, and auto-healing.
- This ensures consistency and prevents duplicating the PMD and compilation steps.
- Step 4 must always run after Step 3 executes or is explicitly skipped.
- Persist the delegated execution summary under `.workflow_state.json` before continuing.
- If delegated validation fails, record the failure and continue to Step 5 with the failure context rather than ending silently.

### Step 5: User Feedback & Accuracy Score

[Delegates to @supporting-files/reporting-engine.md]

- Collect user feedback
- Calculate final Accuracy Score
- Save the accuracy score to `.workflow_state.json`
- Output the final accuracy score to the user
- Step 5 must always run after Step 4 using the latest saved state.
- If user feedback is not yet available, record `userFeedbackStatus = "not_provided"` and emit a provisional final accuracy score instead of skipping reporting.

### Step 6: State Cleanup

- As the absolute final step of the skill's execution, automatically delete the `.workflow_state.json` file from the workspace to ensure a clean environment for future runs.
- Only perform cleanup after Step 5 has saved its final state and the user-facing summary has been produced.

## Non-Negotiable Rules

- **[Workflow control policy]**
  - Every step must write its own completion or blocked status before the workflow moves on or stops.
  - Never treat a missing state key as permission to improvise a value.
  - Never terminate the workflow early without recording the reason and the last completed step.
  - Do not loop indefinitely: if progress now depends on missing locator data or missing user clarification, record that dependency explicitly and hand off or stop.

- **[Watermark policy]**
  - Every affected Java file generated by this skill must contain comments with the invisible watermark defined in `supporting-files/generation-engine.md`.
  - This applies to generated tests, generated page objects, and generated utilities.
  - If an affected file has no comments, insert watermarked JavaDoc comments.
  - If the repository contains no comments, still insert watermarked JavaDoc comments into every affected file handled by this skill.
  - The authoritative enforcement mechanism is the local Python module `scripts/watermark_enforcer.py`, not the Markdown examples.
  - After any generation or locator merge that changes Java code, run the local module with `enforce` first and `validate` second before continuing.
  - Prefer `--state .workflow_state.json --story-slug ${story_slug}` so the enforcer covers `${javaFilePath}`, `${pageObjectPaths}`, and `${utilityPaths}` together; if state is unavailable, pass every affected Java file explicitly with repeated `--file` arguments.
  - Treat the watermark comments as a required output artifact of the skill, not as optional documentation.

- **[Input normalization policy]**
  - Accept story inputs in four supported forms: story/test identifiers, file paths or attachment tokens, referenced workspace story files, and raw pasted test steps or expected-result narratives.
  - Resolve attachment-style inputs before fallback searching.
  - Resolve `STRY...` and `TEMT...` inputs through Now MCP and verify the returned story details before allowing code generation to begin.
  - Do not silently skip generation when an identifier or attachment cannot be resolved; ask the user for the missing story source.
  - Treat input recognition, Now MCP retrieval, and story validation as orchestration responsibilities of `SKILL.md`.

## State Management

All inputs/decisions saved to `.workflow_state.json`:

```json
{
  "story_slug": {
    "inputKind": "story_id | test_id | file | raw_steps",
    "storySource": "now_mcp | workspace_file | chat_input",
    "storyRetrieval": {
      "status": "PASS",
      "identifier": "STRY1234567",
      "source": "now_mcp"
    },
    "javaFilePath": "src/test/java/.../TestClass.java",
    "pageObjectPaths": [...],
    "utilityPaths": [...],
    "pageObjects": [...],
    "utils": [...],
    "instanceUrl": "...",
    "evaluationScores": [...],
    "playwrightDecision": {...},
    "pmdViolations": [...],
    "watermark": {...},
    "userFeedback": "...",
    "finalAccuracyScore": 95
  }
}
```

## Error Recovery

- **Invalid JSON**: Backup + reinitialize `.workflow_state.json`
- **Missing directories**: Auto-create with warning
- **Playwright MCP failure**: Retry once, skip with warning
- **PMD unavailable**: Skip analysis, note in report
- **Blocked workflow**: Save `workflowStatus` with the blocking reason before stopping

## Outputs

- `${discovered_test_directory}/${code_name}.java` - Final test code in project location
- Must include watermarked comments
- `${discovered_pageobject_directory}/*.java` - Page Objects (if created)
- Any generated or modified page object must include watermarked comments
- `${discovered_utility_directory}/*.java` - Utilities (if created)
- Any generated or modified utility must include watermarked comments
- No locator artifact folders/files are generated; locators discovered via Playwright MCP are merged directly into the Java code in memory

## Configuration

Default settings (overridable in `.workflow_state.json`):

```json
{
  "workflow_config": {
    "functional_correctness_threshold": 4,
    "overall_score_threshold": 8,
    "playwright_retry_count": 1,
    "enable_pmd_analysis": true
  }
}
```

---

**Ready to generate. Provide your user story to begin.**
