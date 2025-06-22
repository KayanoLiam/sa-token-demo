#!/bin/bash

# SA-Token Demo 测试运行脚本
# 用于运行各种类型的测试

echo "========================================="
echo "SA-Token Demo 测试运行脚本"
echo "========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 函数：打印带颜色的消息
print_message() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# 函数：运行测试并检查结果
run_test() {
    local test_name=$1
    local test_command=$2
    
    print_message $BLUE "正在运行: $test_name"
    echo "命令: $test_command"
    echo "----------------------------------------"
    
    if eval $test_command; then
        print_message $GREEN "✅ $test_name 通过"
    else
        print_message $RED "❌ $test_name 失败"
        return 1
    fi
    echo ""
}

# 检查Maven是否可用
if ! command -v ./mvnw &> /dev/null; then
    print_message $RED "错误: Maven wrapper (mvnw) 未找到"
    exit 1
fi

# 主菜单
show_menu() {
    echo "请选择要运行的测试类型:"
    echo "1. 运行所有测试"
    echo "2. 运行单元测试"
    echo "3. 运行集成测试"
    echo "4. 运行特定测试类"
    echo "5. 运行测试套件"
    echo "6. 生成测试报告"
    echo "7. 运行测试并生成覆盖率报告"
    echo "8. 清理并重新运行所有测试"
    echo "9. 退出"
    echo ""
}

# 运行特定测试类的菜单
show_test_classes() {
    echo "请选择要运行的测试类:"
    echo "1. UserServiceTest"
    echo "2. AuthControllerTest"
    echo "3. UserControllerTest"
    echo "4. PasswordUtilTest"
    echo "5. UserRepositoryTest"
    echo "6. StpInterfaceImplTest"
    echo "7. UserTest"
    echo "8. ResultTest"
    echo "9. LoginRequestTest"
    echo "10. RegisterRequestTest"
    echo "11. AuthIntegrationTest"
    echo "12. 返回主菜单"
    echo ""
}

# 主循环
while true; do
    show_menu
    read -p "请输入选项 (1-9): " choice
    
    case $choice in
        1)
            print_message $YELLOW "运行所有测试..."
            run_test "所有测试" "./mvnw test"
            ;;
        2)
            print_message $YELLOW "运行单元测试..."
            run_test "单元测试" "./mvnw test -Dtest='**/*Test.java' -Dtest.exclude='**/*IntegrationTest.java'"
            ;;
        3)
            print_message $YELLOW "运行集成测试..."
            run_test "集成测试" "./mvnw test -Dtest='**/*IntegrationTest.java'"
            ;;
        4)
            while true; do
                show_test_classes
                read -p "请输入选项 (1-12): " test_choice
                
                case $test_choice in
                    1)
                        run_test "UserServiceTest" "./mvnw test -Dtest=UserServiceTest"
                        ;;
                    2)
                        run_test "AuthControllerTest" "./mvnw test -Dtest=AuthControllerTest"
                        ;;
                    3)
                        run_test "UserControllerTest" "./mvnw test -Dtest=UserControllerTest"
                        ;;
                    4)
                        run_test "PasswordUtilTest" "./mvnw test -Dtest=PasswordUtilTest"
                        ;;
                    5)
                        run_test "UserRepositoryTest" "./mvnw test -Dtest=UserRepositoryTest"
                        ;;
                    6)
                        run_test "StpInterfaceImplTest" "./mvnw test -Dtest=StpInterfaceImplTest"
                        ;;
                    7)
                        run_test "UserTest" "./mvnw test -Dtest=UserTest"
                        ;;
                    8)
                        run_test "ResultTest" "./mvnw test -Dtest=ResultTest"
                        ;;
                    9)
                        run_test "LoginRequestTest" "./mvnw test -Dtest=LoginRequestTest"
                        ;;
                    10)
                        run_test "RegisterRequestTest" "./mvnw test -Dtest=RegisterRequestTest"
                        ;;
                    11)
                        run_test "AuthIntegrationTest" "./mvnw test -Dtest=AuthIntegrationTest"
                        ;;
                    12)
                        break
                        ;;
                    *)
                        print_message $RED "无效选项，请重新选择"
                        ;;
                esac
            done
            ;;
        5)
            print_message $YELLOW "运行测试套件..."
            run_test "测试套件" "./mvnw test -Dtest=TestSuite"
            ;;
        6)
            print_message $YELLOW "生成测试报告..."
            run_test "测试报告生成" "./mvnw surefire-report:report"
            if [ $? -eq 0 ]; then
                print_message $GREEN "测试报告已生成到: target/site/surefire-report.html"
            fi
            ;;
        7)
            print_message $YELLOW "运行测试并生成覆盖率报告..."
            run_test "测试和覆盖率报告" "./mvnw clean test jacoco:report"
            if [ $? -eq 0 ]; then
                print_message $GREEN "覆盖率报告已生成到: target/site/jacoco/index.html"
            fi
            ;;
        8)
            print_message $YELLOW "清理并重新运行所有测试..."
            run_test "清理和测试" "./mvnw clean test"
            ;;
        9)
            print_message $BLUE "退出测试脚本"
            exit 0
            ;;
        *)
            print_message $RED "无效选项，请重新选择"
            ;;
    esac
    
    # 询问是否继续
    echo ""
    read -p "按 Enter 键继续，或输入 'q' 退出: " continue_choice
    if [ "$continue_choice" = "q" ]; then
        print_message $BLUE "退出测试脚本"
        exit 0
    fi
    echo ""
done
