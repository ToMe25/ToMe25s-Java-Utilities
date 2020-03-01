import os
import shutil

def main():
    # move to the git-hooks directory.
    if(os.path.basename(os.getcwd()) != "git-hooks"):
        os.chdir(os.path.join(os.getcwd(), "git-hooks"))
    
    # copy git hooks to .get/hooks.
    githooks = os.path.join(os.path.join(os.path.dirname(os.getcwd()), ".git"), "hooks")
    for root, directories, files in os.walk(os.getcwd()):
        for file in files:
            if(file.endswith(".py") == False):
                shutil.copyfile(os.path.join(root, file), os.path.join(githooks, file))
    
    # print a secess message.
    print("installed all the git hooks.")

if __name__ == '__main__':
    main()
