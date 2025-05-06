# Test Resources Directory

This directory contains test resources used by the test suite.

## Required Files

1. `demo-audio.mp3` - Audio file for lip sync testing
2. `video.mp4` - Video file for lip sync testing

## File Requirements

### Audio File (demo-audio.mp3)
- Format: MP3
- Duration: 30 seconds or less recommended
- Size: Less than 10MB recommended
- Content: Should contain clear speech for lip sync testing

### Video File (video.mp4)
- Format: MP4
- Resolution: 1080p or lower recommended
- Duration: 30 seconds or less recommended
- Size: Less than 50MB recommended
- Content: Should contain a person speaking for lip sync testing

## How to Add Files

1. Place your test files in this directory
2. Make sure the files are named exactly as specified above
3. Ensure the files meet the requirements listed above

## CI/CD Setup

For CI/CD environments, the test files are downloaded during the workflow execution. Make sure to:
1. Host your test files in a secure, accessible location
2. Update the URLs in the CI workflow file to point to your actual test files
3. Ensure the files are publicly accessible or use appropriate authentication

Note: These files are used for testing purposes only and should not be committed to the repository if they contain sensitive or proprietary content. 