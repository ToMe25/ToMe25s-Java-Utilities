import os
import shutil
import fileinput

def main():
    # move to the repository root directory.
    if(os.path.basename(os.getcwd()) == "git-hooks"):
        os.chdir(os.path.dirname(os.getcwd()))
    elif(os.path.basename(os.getcwd()) == "hooks"):
        os.chdir(os.path.dirname(os.path.dirname(os.getcwd())))
    
    # create tmp dir to compile the source in.
    tmpdir = os.path.join(os.getcwd(), "tmp")
    os.mkdir(tmpdir)
    
    # get all files to compile.
    path = os.path.join(os.getcwd(), "src")
    sources = ""
    for root, directories, files in os.walk(path):
        for file in files:
            if(file.endswith(".java")):
                sources += os.path.join(root, file) + " "
    
    # compile the sources int tmp.
    os.system("javac -d tmp " + sources)
    
    # copy resources to tmp.
    for root, directories, files in os.walk(path):
        for file in files:
            if(file.endswith(".java") == False and os.path.basename(file) != ".directory"):
                shutil.copyfile(os.path.join(root, file), os.path.join(tmpdir, file))
    
    # copy LICENSE to tmp.
    shutil.copyfile(os.path.join(os.getcwd(), "LICENSE"), os.path.join(tmpdir, "LICENSE"))
    
    # update the version number.
    file = open("MANIFEST.MF", "rwt+")
    for line in fileinput.input("MANIFEST.MF", inplace=True):
        #line = line[:-1]
        if(line.startswith("Version")):
            index = line.index('.') + 1
            line = line[:index] + str(int(line[index:]) + 1)
        print(line)
    
    # add the modified MANIFEST.MF file to the commit.
    os.system("git add ToMe25s-Java-Utilities.jar")
    
    # pack tmp into a jar.
    os.system("jar -cfm ToMe25s-Java-Utilities.jar MANIFEST.MF -C tmp .")
    
    # remove the tmp directory.
    shutil.rmtree(tmpdir)
    
    # add the compiled file to the commit.
    os.system("git add ToMe25s-Java-Utilities.jar")
    
    # print a secess message.
    print("Successfully compiled ToMe25s-Java-Utilities.")

if __name__ == '__main__':
    main()
