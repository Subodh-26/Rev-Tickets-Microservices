@echo off
echo Creating ECR repositories...

aws ecr create-repository --repository-name revtickets-backend --region us-east-1
aws ecr create-repository --repository-name revtickets-frontend --region us-east-1

echo Done!
echo.
echo Note your repository URIs from the output above.
pause
