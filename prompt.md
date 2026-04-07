# Selenium Test Generation

## What I Do

Generate production-ready Java JUnit Selenium tests from ServiceNow user stories with:

## Quick Start

**For New Tests:**
  - Raw test steps and expected results pasted directly in chat

## Workflow

### Step 1: Generate Selenium Java (JUnit)


- Consumes the already normalized and validated story details prepared in Step 0
- Discovers project structure (test/pageobject/utility directories)
- Generates Java JUnit test with Page Objects
- Applies the mandatory invisible-watermark comment policy to every affected generated Java file.
- Resolves the skill directory containing `scripts/watermark_enforcer.py` and runs the local module in `enforce` mode followed immediately by `validate` mode after every Java write performed by this skill.
- Confirms PO/Utils selection with user
- Saves directly to proper project locations
- Step 1 is complete only when all affected Java files are written, watermark `enforce` and `validate` both pass, `javaFilePath` plus any `pageObjectPaths` and `utilityPaths` are saved, and `workflowStatus.currentStep = "step1"` is recorded.

**Ready to generate. Provide your user story to begin.**
