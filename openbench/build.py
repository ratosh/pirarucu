import subprocess, platform, os, zipfile, shutil

# Windows treated seperatly from Linux
IS_WINDOWS = platform.system() == 'Windows'

command = 'gradlew clean build'

if not IS_WINDOWS:
    os.system('chmod +x ../gradlew')
    command = './{0}'.format(command)

process = subprocess.Popen(
    command.split(),
    cwd='../',
    shell=IS_WINDOWS)
process.communicate()

for file in os.listdir("../build/distributions"):
    if file.endswith(".zip") and "jvm" in file:
        print("Found zip {0}".format(file))
        fileName = os.path.splitext(file)[0]
        with zipfile.ZipFile("../build/distributions/{0}".format(file)) as data:
            data.extractall("../build/distributions/")
        shutil.move("../build/distributions/{0}".format(fileName), "../build/output/")
