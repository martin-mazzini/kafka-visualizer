git init
git add .
git commit -m %1
git remote add origin %2
git pull origin master --allow-unrelated-histories
git push -u origin master