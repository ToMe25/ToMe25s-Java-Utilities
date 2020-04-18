import os
import shutil
import fileinput

def main():
    # move to the repository root directory
    if(os.path.basename(os.getcwd()) == "git-hooks"):
        os.chdir(os.path.dirname(os.getcwd()))
    elif(os.path.basename(os.getcwd()) == "hooks"):
        os.chdir(os.path.dirname(os.path.dirname(os.getcwd())))

    # dont do anything if the keystore file doesn't exists
    if(os.path.exists(os.path.join(os.getcwd(), ".Keys.jks")) == False):
        print("Don't compile ToMe25s-Java-Utilites as the .Keys.jks keystore doesn't exist.")
        quit()

    # execute maven build
    os.system("mvn -B package")

    # ove the archives into the root directory
    targetdir = os.path.join(os.getcwd(), "target")
    for root, directories, files in os.walk(targetdir):
        for file in files:
            if(file.startswith("ToMe25s-Java-Utilities")):
                if(file.endswith("sources.jar")):
                    shutil.copyfile(os.path.join(targetdir, file), os.path.join(os.getcwd(), "ToMe25s-Java-Utilities-sources.jar"))
                    os.remove(os.path.join(targetdir, file))
                elif(file.endswith("javadoc.jar")):
                    shutil.copyfile(os.path.join(targetdir, file), os.path.join(os.getcwd(), "ToMe25s-Java-Utilities-javadoc.jar"))
                    os.remove(os.path.join(targetdir, file))
                elif(file.endswith(".jar")):
                    shutil.copyfile(os.path.join(targetdir, file), os.path.join(os.getcwd(), "ToMe25s-Java-Utilities.jar"))
                    os.remove(os.path.join(targetdir, file))

    # add the archives to the commit
    os.system("git add ToMe25s-Java-Utilities.jar")
    os.system("git add ToMe25s-Java-Utilities-javadoc.jar")
    os.system("git add ToMe25s-Java-Utilities-sources.jar")

    # remove old javadocs
    javadocdir = os.path.join(os.getcwd(), "javadoc")
    if(os.path.exists(javadocdir)):
        os.system("git rm -rf " + javadocdir)
        shutil.rmtree(javadocdir) # archives seem to survive git rm, so they get removed here

    # copy javadocs into root directory
    shutil.copytree(os.path.join(targetdir, "apidocs"), os.path.join(os.getcwd(), "javadoc"))

    # add javadoc directory to the commit
    os.system("git add " + javadocdir)

    # print a secess message
    print("Successfully compiled ToMe25s-Java-Utilities.")

if __name__ == '__main__':
    main()
