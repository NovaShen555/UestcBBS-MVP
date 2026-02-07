#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Discourse API 通用测试脚本
固定登录前置 + 按需请求 API 并返回结构化结果
"""

import requests
import json
from typing import Optional, Dict, Any

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

    def login_and_get_session(self, username: str, password: str) -> Optional[requests.Session]:
        """固定前置：登录并返回携带 Cookie 的会话"""
        if not self.get_csrf_token():
            return None
        if not self.login(username, password):
            return None
        return self.session

    def request_api(self,
                    method: str,
                    path: str,
                    params: Optional[Dict[str, Any]] = None,
                    data: Optional[Dict[str, Any]] = None,
                    json_body: Optional[Dict[str, Any]] = None,
                    headers: Optional[Dict[str, str]] = None,
                    timeout: int = 20) -> Dict[str, Any]:
        """通用 API 请求，返回结构化结果便于 agent 解析"""
        url = f"{self.base_url}/{path.lstrip('/')}"
        method = method.upper().strip()

        merged_headers = dict(self.session.headers)
        if headers:
            merged_headers.update(headers)

        try:
            response = self.session.request(
                method=method,
                url=url,
                params=params,
                data=data,
                json=json_body,
                headers=merged_headers,
                timeout=timeout
            )

            try:
                body = response.json()
            except Exception:
                body = response.text

            return {
                "ok": response.ok,
                "status_code": response.status_code,
                "url": response.url,
                "headers": dict(response.headers),
                "body": body,
                "cookies": dict(self.session.cookies),
            }
        except Exception as e:
            return {
                "ok": False,
                "status_code": None,
                "url": url,
                "headers": {},
                "body": str(e),
                "cookies": dict(self.session.cookies),
            }



def main():
    # 配置信息
    BASE_URL = "https://river-side.cc"
    USERNAME = "NoahShen"
    PASSWORD = "SYHhyh240507"

    # 创建测试器并登录
    tester = DiscourseAPITester(BASE_URL)
    session = tester.login_and_get_session(USERNAME, PASSWORD)
    if session is None:
        print("\n✗ 登录流程失败，无法继续测试")
        return

    # 示例：根据需要修改测试目标
    api_result = tester.request_api(
        method="GET",
        path="/site.json",
        params=None,
        data=None,
        json_body=None
    )

    print("\n=== API 测试结果 ===")
    print(json.dumps(api_result, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
