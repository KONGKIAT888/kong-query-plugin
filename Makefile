# KongQuery IntelliJ Plugin Deployment Makefile
# Usage: make [target]

.PHONY: help build test run clean verify version setversion release info

# Default target
.DEFAULT_GOAL := help

# Variables
PLUGIN_NAME := KongQuery
PLUGIN_VERSION := $(shell grep 'version = ' build.gradle.kts | sed 's/.*"\([^"]*\)".*/\1/')
PLUGIN_ID := me.kongkiat.kongquery
BUILD_DIR := build/distributions
GRADLEW := ./gradlew

# Colors for output
RED := \033[0;31m
GREEN := \033[0;32m
YELLOW := \033[0;33m
BLUE := \033[0;34m
PURPLE := \033[0;35m
CYAN := \033[0;36m
WHITE := \033[0;37m
RESET := \033[0m

help: ## Show this help message
	@echo "$(CYAN)🦍 KongQuery Plugin Deployment Makefile$(RESET)"
	@echo "$(YELLOW)Current Version: $(PLUGIN_VERSION)$(RESET)"
	@echo ""
	@echo "$(WHITE)Available targets:$(RESET)"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  $(GREEN)%-15s$(RESET) %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo ""

build: ## Build the plugin (gradle buildPlugin)
	@echo "$(BLUE)🔨 Building $(PLUGIN_NAME) plugin v$(PLUGIN_VERSION)...$(RESET)"
	@$(GRADLEW) clean buildPlugin
	@echo "$(GREEN)✅ Build completed successfully!$(RESET)"
	@echo "$(CYAN)📦 Plugin artifacts created in: $(BUILD_DIR)$(RESET)"

test: ## Run tests
	@echo "$(BLUE)🧪 Running tests...$(RESET)"
	@$(GRADLEW) test

run: ## Run plugin in development mode (gradle runIde)
	@echo "$(BLUE)🚀 Starting IntelliJ IDEA with plugin...$(RESET)"
	@$(GRADLEW) runIde

clean: ## Clean build artifacts
	@echo "$(YELLOW)🧹 Cleaning build artifacts...$(RESET)"
	@$(GRADLEW) clean
	@$(GRADLEW) cleanSandbox

verify: ## Verify plugin before release (build + test)
	@echo "$(PURPLE)🔍 Verifying plugin for release...$(RESET)"
	@$(MAKE) test
	@$(MAKE) build

version: ## Check if version is updated from last tag
	@echo "$(BLUE)📋 Checking version status...$(RESET)"
	@LAST_TAG=$$(git describe --tags --abbrev=0 2>/dev/null || echo "v0.0.0"); \
	CURRENT_VERSION="$(PLUGIN_VERSION)"; \
	if [ "$$LAST_TAG" = "v$$CURRENT_VERSION" ]; then \
		echo "$(RED)❌  Version $$CURRENT_VERSION already exists as tag $$LAST_TAG$(RESET)"; \
		echo "$(YELLOW)⚠️  Please update version in build.gradle.kts$(RESET)"; \
		exit 1; \
	else \
		echo "$(GREEN)✅  Version $$CURRENT_VERSION is newer than last tag $$LAST_TAG$(RESET)"; \
	fi

set: ## Set new version (usage: make setversion VERSION=25.4.5)
	@if [ -z "$(VERSION)" ]; then \
		echo "$(RED)❌ Please specify version: make setversion VERSION=25.4.5$(RESET)"; \
		exit 1; \
	fi
	@echo "$(BLUE)🔄 Setting version to $(VERSION)...$(RESET)"
	@if grep -q "version = " build.gradle.kts; then \
		sed -i '' 's/version = "[^"]*"/version = "$(VERSION)"/' build.gradle.kts; \
		echo "$(GREEN)✅ Updated build.gradle.kts to v$(VERSION)$(RESET)"; \
	else \
		echo "$(RED)❌ Could not find version line in build.gradle.kts$(RESET)"; \
		exit 1; \
	fi
	@echo "$(CYAN)📋 Version change summary:$(RESET)"
	@echo "  - Old version: $(PLUGIN_VERSION)"
	@echo "  - New version: $(VERSION)"
	@echo ""
	@echo "$(YELLOW)💡 Don't forget to:$(RESET)"
	@echo "  1. Add changelog entry in CHANGELOG.md"
	@echo "  2. Run 'make verify' to test the build"
	@echo "  3. Run 'make release' when ready"

release: ## Full release process (tag, push, and prepare artifacts)
	@echo "$(PURPLE)🚀 Starting Release Process for v$(PLUGIN_VERSION)$(RESET)"
	@echo "$(RED)⚠️ This will create and push a git tag!$(RESET)"
	@echo ""
	@$(MAKE) version
	@$(MAKE) verify
	@echo ""
	@echo "$(YELLOW)📋 Ready to create release for v$(PLUGIN_VERSION)$(RESET)"
	@read -p "Continue with tagging and pushing? (y/N) " confirm && [ "$$confirm" = "y" ] || exit 1
	@echo ""
	@echo "$(BLUE)🏷️  Creating git tag v$(PLUGIN_VERSION)...$(RESET)"
	@git tag v$(PLUGIN_VERSION)
	@echo "$(BLUE)📤 Pushing tag to origin...$(RESET)"
	@git push origin v$(PLUGIN_VERSION)
	@echo ""
	@echo "$(GREEN)✅ Release tagged and pushed successfully!$(RESET)"
	@echo "$(CYAN)📋 Next steps:$(RESET)"
	@echo "  1. Upload plugin to JetBrains Marketplace:"
	@echo "     - Plugin file: $(BUILD_DIR)/$(PLUGIN_NAME)-$(PLUGIN_VERSION).zip"
	@echo "  2. Create GitHub Release:"
	@echo "     - Upload the same .zip file to GitHub Releases"
	@echo "     - Link to the changelog entry"

info: ## Show build and deployment information
	@echo "$(CYAN)📋 Build & Deployment Information$(RESET)"
	@echo ""
	@echo "$(WHITE)Project Details:$(RESET)"
	@echo "  - Name: $(PLUGIN_NAME)"
	@echo "  - Version: $(PLUGIN_VERSION)"
	@echo "  - Plugin ID: $(PLUGIN_ID)"
	@echo "  - Build Tool: Gradle with Kotlin DSL"
	@echo "  - IntelliJ Platform: 2025.1.4.1+"
	@echo ""
	@echo "$(WHITE)Quick Commands:$(RESET)"
	@echo "  - Build:     make build"
	@echo "  - Test:      make test"
	@echo "  - Run:       make run"
	@echo "  - Clean:     make clean"
	@echo "  - Release:   make release"
	@echo "  - Verify:    make verify"
	@echo ""
	@echo "$(WHITE)Marketplace:$(RESET)"
	@echo "  - Plugin URL: https://plugins.jetbrains.com/plugin/28845-kongquery"