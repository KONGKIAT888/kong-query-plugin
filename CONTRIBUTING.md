# Contributing to CONTRIBUTING.md

If you want to contribute to this file, please follow these guidelines:

1. Fork the repository.
2. Create a new branch.
3. Make your changes.
4. Commit your changes.
5. Push your changes to your fork.
6. Create a pull request.

### Local Development

To build and run locally:

```bash
./gradlew buildPlugin
./gradlew runIde
```

Plugin ID: `me.kongkiat.kongquery`
Root project: `kongquery`

### 🚀 Releasing a New Version

When releasing a new version, update the following files:

#### 1. Update Version Number
**File**: `build.gradle.kts`
```kotlin
version = "25.4.1"  // Increment this
```

#### 2. Update Changelog
**File**: `CHANGELOG.md`
```markdown
## v25.4.1

- **New:** Add new features here
- **Enhanced:** Improvements to existing features
- **Fixed:** Bug fixes
- **Documentation:** Documentation updates

---
```

#### 3. Update README (if needed)
**File**: `README.md`
- Add new features to the Features section
- Update usage examples if functionality changed
- **Note**: Avoid adding version-specific "What's New" sections - use CHANGELOG.md instead

#### 4. Build and Test
```bash
# Clean build
./gradlew clean build

# Test plugin
./gradlew runIde

# Build plugin package
./gradlew buildPlugin
```

#### 5. Release Process
1. Commit all changes
2. Create git tag: `git tag v25.4.3`
3. Push tag: `git push origin v25.4.3`
4. Upload plugin to JetBrains Marketplace
5. Create GitHub Release with built plugin `.zip`

### 📋 Release Checklist

- [ ] Update version in `build.gradle.kts`
- [ ] Add changelog entry in `CHANGELOG.md`
- [ ] Update README.md if features changed (Optional)
- [ ] Run `./gradlew clean build` successfully
- [ ] Test plugin functionality
- [ ] Create git tag and push
- [ ] Upload to Marketplace
- [ ] Create GitHub Release

---