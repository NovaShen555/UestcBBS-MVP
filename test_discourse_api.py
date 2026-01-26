#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Discourse API 测试脚本
测试完整的登录和发帖流程
"""

import requests
import json
from typing import Optional

class DiscourseAPITester:
    def __init__(self, base_url: str):
        self.base_url = base_url.rstrip('/')
        self.session = requests.Session()
        self.csrf_token: Optional[str] = None

        # 设置默认 headers
        self.session.headers.update({
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
            'Accept': 'application/json',
            'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8',
        })

    def get_csrf_token(self) -> bool:
        """获取 CSRF token"""
        print("\n=== 步骤 1: 获取 CSRF Token ===")
        url = f"{self.base_url}/session/csrf"

        try:
            response = self.session.get(url)
            print(f"请求 URL: {url}")
            print(f"响应状态码: {response.status_code}")
            print(f"响应头: {dict(response.headers)}")

            if response.status_code == 200:
                data = response.json()
                self.csrf_token = data.get('csrf')
                print(f"✓ CSRF Token: {self.csrf_token}")
                print(f"✓ Cookies: {dict(self.session.cookies)}")
                return True
            else:
                print(f"✗ 获取 CSRF token 失败: {response.text}")
                return False
        except Exception as e:
            print(f"✗ 异常: {e}")
            return False

    def login(self, username: str, password: str) -> bool:
        """登录"""
        print("\n=== 步骤 2: 登录 ===")
        url = f"{self.base_url}/session"

        # 设置 CSRF token 到 header
        if self.csrf_token:
            self.session.headers.update({
                'X-CSRF-Token': self.csrf_token
            })

        payload = {
            'login': username,
            'password': password
        }

        try:
            print(f"请求 URL: {url}")
            print(f"请求头: {dict(self.session.headers)}")
            print(f"请求体: {payload}")

            response = self.session.post(url, data=payload)
            print(f"响应状态码: {response.status_code}")
            print(f"响应头: {dict(response.headers)}")
            print(f"响应体: {response.text[:500]}")

            if response.status_code == 200:
                data = response.json()
                user = data.get('user', {})
                print(f"✓ 登录成功！")
                print(f"  用户名: {user.get('username')}")
                print(f"  用户ID: {user.get('id')}")
                print(f"✓ Cookies: {dict(self.session.cookies)}")
                return True
            else:
                print(f"✗ 登录失败: {response.text}")
                return False
        except Exception as e:
            print(f"✗ 异常: {e}")
            return False

    def create_post(self, topic_id: int, content: str, category_id: Optional[int] = None,
                    reply_to_post_number: Optional[int] = None) -> bool:
        """发表评论"""
        print("\n=== 步骤 3: 发表评论 ===")
        url = f"{self.base_url}/posts"

        payload = {
            'raw': content,
            'topic_id': topic_id,
            'archetype': 'regular',
            'nested_post': True
        }

        if category_id is not None:
            payload['category'] = category_id

        if reply_to_post_number is not None:
            payload['reply_to_post_number'] = reply_to_post_number

        try:
            print(f"请求 URL: {url}")
            print(f"请求头: {dict(self.session.headers)}")
            print(f"请求体: {json.dumps(payload, ensure_ascii=False, indent=2)}")
            print(f"Cookies: {dict(self.session.cookies)}")

            response = self.session.post(url, json=payload)
            print(f"\n响应状态码: {response.status_code}")
            print(f"响应头: {dict(response.headers)}")
            print(f"响应体: {response.text}")

            if response.status_code == 200:
                data = response.json()
                print(f"\n✓ 发表评论成功！")
                print(f"  帖子ID: {data.get('id')}")
                print(f"  楼层号: {data.get('post_number')}")
                return True
            else:
                print(f"\n✗ 发表评论失败")
                print(f"  状态码: {response.status_code}")
                print(f"  错误信息: {response.text}")
                return False
        except Exception as e:
            print(f"✗ 异常: {e}")
            import traceback
            traceback.print_exc()
            return False

    def test_full_flow(self, username: str, password: str, topic_id: int,
                       content: str, category_id: Optional[int] = None):
        """测试完整流程"""
        print("=" * 60)
        print("Discourse API 完整流程测试")
        print("=" * 60)

        # 步骤 1: 获取 CSRF token
        if not self.get_csrf_token():
            print("\n✗ 测试失败：无法获取 CSRF token")
            return False

        # 步骤 2: 登录
        if not self.login(username, password):
            print("\n✗ 测试失败：登录失败")
            return False

        # 步骤 3: 发表评论
        if not self.create_post(topic_id, content, category_id):
            print("\n✗ 测试失败：发表评论失败")
            return False

        print("\n" + "=" * 60)
        print("✓ 所有测试通过！")
        print("=" * 60)
        return True


def main():
    # 配置信息
    BASE_URL = "https://river-side.cc"
    USERNAME = input("请输入用户名: ").strip()
    PASSWORD = input("请输入密码: ").strip()
    TOPIC_ID = 2056
    CONTENT = "这是一条测试评论，用于调试 API 接口。[测试时间: 2026-01-26]"

    # 可选：如果知道分类ID，可以填写
    CATEGORY_ID = None  # 例如: 1, 2, 3 等

    # 创建测试器并运行
    tester = DiscourseAPITester(BASE_URL)
    tester.test_full_flow(USERNAME, PASSWORD, TOPIC_ID, CONTENT, CATEGORY_ID)


if __name__ == "__main__":
    main()
