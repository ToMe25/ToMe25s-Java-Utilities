import os
import shutil
import fileinput

def main():
    # move to the repository root directory.
    if(os.path.basename(os.getcwd()) == "git-hooks"):
        os.chdir(os.path.dirname(os.getcwd()))
    elif(os.path.basename(os.getcwd()) == "hooks"):
        os.chdir(os.path.dirname(os.path.dirname(os.getcwd())))
    
    if(os.path.exists(os.path.join(os.getcwd(), ".Keys.jks")) == False):
        print("Don't compile ToMe25s-Java-Utilites as the .Keys.jks keystore doesn't exist.")
        quit()
    
    # create tmp dir to compile the source in.
    tmpdir = os.path.join(os.getcwd(), "tmp")
    os.mkdir(tmpdir)
    
    # get all files to compile.
    srcdir = os.path.join(os.getcwd(), "src")
    sources = ""
    for root, directories, files in os.walk(srcdir):
        for file in files:
            if(file.endswith(".java")):
                sources += os.path.join(root, file) + " "
    
    # compile the sources int tmp.
    os.system("javac -g -parameters -d tmp " + sources)
    
    # copy resources to tmp.
    for root, directories, files in os.walk(srcdir):
        for file in files:
            if(file.endswith(".java") == False and os.path.basename(file) != ".directory"):
                shutil.copyfile(os.path.join(root, file), os.path.join(tmpdir, file))
    
    # copy LICENSE to tmp.
    shutil.copyfile(os.path.join(os.getcwd(), "LICENSE"), os.path.join(tmpdir, "LICENSE"))
    
    # update the version number.
    for line in fileinput.input("MANIFEST.MF", inplace=True):
        #line = line[:-1]
        if(line.startswith("ToMe25s-Java-Utilities-Version:")):
            index = line.index('.') + 1
            line = line[:index] + str(int(line[index:]) + 1)
        print(line)
    
    # add the modified MANIFEST.MF file to the commit.
    os.system("git add MANIFEST.MF")
    
    # pack tmp into a jar.
    os.system("jar -cfm ToMe25s-Java-Utilities.jar MANIFEST.MF -C tmp .")
    
    # read the keystore password.
    file = open(".KeysPWD", "rt")
    password = file.readline()
    
    # sign the new jar.
    os.system("jarsigner -keystore .Keys.jks -storepass " + password + " ToMe25s-Java-Utilities.jar Utilities")
    
    # add the compiled file to the commit.
    os.system("git add ToMe25s-Java-Utilities.jar")
    
    # remove old javadocs
    javadocdir = os.path.join(os.getcwd(), "javadoc")
    if(os.path.exists(javadocdir)):
        shutil.rmtree(javadocdir)
    
    # create new javadocs
    os.mkdir(javadocdir)
    os.system("javadoc -quiet -private -notimestamp -d javadoc -sourcepath " + srcdir + " -subpackages com.tome25.utils")
    
    # add javadoc directory to the commit
    os.system("git add " + javadocdir)
    
    # package the javadoc files into a jar file
    os.system("jar -cf ToMe25s-Java-Utilities-javadoc.jar -C javadoc . LICENSE")
    
    # add the javadoc archive to the commit
    os.system("git add ToMe25s-Java-Utilities-javadoc.jar")
    
    # remove the tmp directory.
    shutil.rmtree(tmpdir)
    
    # print a secess message.
    print("Successfully compiled ToMe25s-Java-Utilities.")

if __name__ == '__main__':
    main()
