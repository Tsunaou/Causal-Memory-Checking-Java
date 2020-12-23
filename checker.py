import os
import glob
import logging
import subprocess
import getpass

DEBUG = True
OVERWRITE = True

logging.basicConfig(level = logging.INFO,format = '%(asctime)s - %(message)s')
logger = logging.getLogger(__name__)

USER = getpass.getuser()
CHECKER_NAME = "Causal-Memory-Checking-Java-jar-with-dependencies.jar"
TARGET = "/home/young/DisAlg/Causal-Consistency/Causal-Memory-Checking-Java/target/" if USER == "young" else "/home/ouyanghongrong/Causal-Memory-Checking-Java/target/"
CHECKER = TARGET + CHECKER_NAME
SELECTED_PATH = "/home/{}/selected-data/".format(USER)


def check_command(history, concurrency, cc_type):
    command = "java -jar {} {} {} {}".format(CHECKER, history, concurrency, cc_type)
    return command

def check_log(msg):
    if DEBUG:
        logger.info(msg)
    else:
        print(msg)

if __name__ == '__main__':
    for type_dir_name in os.listdir(SELECTED_PATH):
        type_dir = SELECTED_PATH + type_dir_name + "/" # eg: /home/young/selected-data/majority_stable/
        check_log("Checking data in {}".format(type_dir_name))
        check_type = "CC"

        for label_data_dir_name in os.listdir(type_dir):
            check_log("Checking data in {}".format(label_data_dir_name))
            check_path = type_dir + label_data_dir_name + "/"
            for history in glob.glob(check_path+"*.edn"):
                check_log("Checking {}".format(history))
                command = check_command(history, 10, check_type)
                result = history.replace("history", "result"+check_type)
                if OVERWRITE and os.path.exists(result):
                    os.remove(result)
                check_cmd = ("time {} | tee {}".format(command, result))
                check_log("Executing {}".format(check_cmd))
                with subprocess.Popen(check_cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                                      encoding="utf-8") as process:
                    stdout = process.stdout.readlines()
                    for line in stdout:
                        print(line)

        print("-"*128)
