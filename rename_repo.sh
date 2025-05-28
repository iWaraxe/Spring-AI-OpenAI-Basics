#!/bin/bash

# Script to update local repository after GitHub rename
# Run this after renaming the repository on GitHub from Spring-AI-OpenAI-Basics to L2ChatModel

echo "Updating remote URL to reflect new repository name..."
git remote set-url origin https://github.com/iWaraxe/L2ChatModel.git

echo "Verifying new remote URL..."
git remote -v

echo "Testing connection to new repository..."
git fetch origin

echo "Repository remote URL updated successfully!"
echo "Your local repository now points to: https://github.com/iWaraxe/L2ChatModel.git"